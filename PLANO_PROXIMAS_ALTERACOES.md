# 📋 Plano de Próximas Alterações - LojaSocial

**Data:** 2026-01-12
**Status:** Em Desenvolvimento

---

## ✅ Concluído

### Fase 1: Stock Management UI ✅
- [x] Criar repositórios (Beneficiary, Family, Agenda, Item)
- [x] Registrar repositories no AppModule
- [x] Stock Overview View + ViewModel
- [x] Stock Lots Detail View + ViewModel
- [x] Stock Moves History View + ViewModel
- [x] Dashboard melhorado (indicadores + alertas)
- [x] MoreMenuView (menu "Mais")
- [x] Bottom bar atualizada (6 tabs)
- [x] Componentes reutilizáveis (StatusBadge, StatsCard)

### Proteção de Acesso ✅
- [x] Bloquear criação/edição de produtos para user
- [x] ProductsView: Botão "+" só para admin
- [x] ProductDetailView: Modo leitura para user
- [x] UserNavGraph: Apenas rotas permitidas

---

## 🎯 Próximas Prioridades

### **FASE 2: Melhorias nas Listas Existentes** (ALTA PRIORIDADE)

---

## 📝 Tarefa 1: Adicionar Status a Donations

### **Objetivo**
Adicionar sistema de estados às doações para rastrear o fluxo: Pendente → Recebida → Processada

### **Modificações no Modelo**

**Arquivo:** `models/Donation.kt`

```kotlin
@IgnoreExtraProperties
data class Donation(
    var docId: String? = null,
    var donorId: String? = null,
    var donorName: String? = null,
    var date: Timestamp? = null,
    var notes: String? = null,

    // NOVOS CAMPOS
    var status: String? = "PENDING",  // "PENDING", "RECEIVED", "PROCESSED"
    var donationId: String? = null,   // ID sequencial tipo "DOA-001"
    var createdAt: Timestamp? = null,
    var updatedAt: Timestamp? = null
)
```

### **Estados Possíveis**

| Estado | Cor | Descrição |
|--------|-----|-----------|
| `PENDING` | 🟠 Laranja (#FFA726) | Doação registada, aguardando triagem |
| `RECEIVED` | 🟢 Verde (#66BB6A) | Doação recebida fisicamente |
| `PROCESSED` | 🔵 Azul (#42A5F5) | Doação processada, stock atualizado |

### **Modificações na UI**

**Arquivo:** `ui/admin/donations/DonationsListView.kt`

**Adicionar:**
1. **Badge de status** em cada card de doação
2. **Filtro por status** (dropdown)
3. **Contador de itens** na doação
4. **ID sequencial** (DOA-XXX)
5. **Botão para mudar status** (dentro do card ou no detalhe)

**Layout sugerido para cada card:**
```
┌─────────────────────────────────────┐
│ DOA-001             [Badge Status]   │
│ João Silva                           │
│ 10/01/2026 • 3 itens                │
│                                      │
│ [Ver Detalhes]                      │
└─────────────────────────────────────┘
```

### **Componente Novo**

**Arquivo:** `ui/admin/components/DonationStatusBadge.kt`

```kotlin
@Composable
fun DonationStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (status) {
        "PENDING" -> "Triagem" to Color(0xFFFFA726)
        "RECEIVED" -> "Recebida" to Color(0xFF66BB6A)
        "PROCESSED" -> "Processada" to Color(0xFF42A5F5)
        else -> "Desconhecido" to Color.Gray
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
```

### **Geração de ID Sequencial**

**Lógica sugerida:**
```kotlin
// No DonationRepository ou ViewModel
suspend fun generateDonationId(): String {
    val year = Calendar.getInstance().get(Calendar.YEAR)
    val snapshot = db.collection("donations")
        .orderBy("createdAt", Query.Direction.DESCENDING)
        .limit(1)
        .get()
        .await()

    val lastId = snapshot.documents.firstOrNull()?.get("donationId") as? String
    val lastNumber = lastId?.substringAfterLast("-")?.toIntOrNull() ?: 0
    val newNumber = lastNumber + 1

    return "DOA-$year-${newNumber.toString().padStart(3, '0')}"
}
```

### **Filtro por Status**

**Adicionar ao ViewModel:**
```kotlin
data class DonationsListState(
    val donations: List<Donation> = emptyList(),
    val filteredDonations: List<Donation> = emptyList(),
    val selectedStatus: String? = null,  // null = ALL
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

fun filterByStatus(status: String?) {
    _state.update {
        it.copy(
            selectedStatus = status,
            filteredDonations = applyFilters(it.donations, status, it.searchQuery)
        )
    }
}

private fun applyFilters(
    donations: List<Donation>,
    status: String?,
    searchQuery: String
): List<Donation> {
    var filtered = donations

    // Filtrar por status
    if (status != null) {
        filtered = filtered.filter { it.status == status }
    }

    // Filtrar por pesquisa
    if (searchQuery.isNotBlank()) {
        filtered = filtered.filter {
            it.donorName?.contains(searchQuery, ignoreCase = true) == true ||
            it.donationId?.contains(searchQuery, ignoreCase = true) == true
        }
    }

    return filtered
}
```

### **Atualização Automática de Status**

**Quando criar doação:**
- Status inicial: `PENDING`
- Gerar `donationId`
- Definir `createdAt`

**Quando processar doação (criar stock):**
- Mudar status para `PROCESSED`
- Atualizar `updatedAt`

---

## 👥 Tarefa 2: Adicionar Status a Beneficiaries

### **Objetivo**
Adicionar estados aos beneficiários e mostrar última entrega

### **Modificações no Modelo**

**Arquivo:** `models/Beneficiary.kt`

```kotlin
@IgnoreExtraProperties
data class Beneficiary(
    var docId: String? = null,
    var name: String? = null,
    var email: String? = null,
    var phone: String? = null,
    var address: String? = null,
    var familyId: String? = null,
    var familySize: Int? = null,

    // NOVOS CAMPOS
    var status: String? = "ACTIVE",  // "ACTIVE", "PENDING", "INACTIVE"
    var lastDeliveryDate: Timestamp? = null,
    var createdAt: Timestamp? = null,
    var updatedAt: Timestamp? = null
)
```

### **Estados Possíveis**

| Estado | Cor | Descrição |
|--------|-----|-----------|
| `ACTIVE` | 🟢 Verde (#66BB6A) | Beneficiário ativo |
| `PENDING` | 🟠 Laranja (#FFA726) | Aguardando validação |
| `INACTIVE` | ⚪ Cinza (#9E9E9E) | Inativo |

### **Modificações na UI**

**Arquivo:** `ui/beneficiary/BeneficiariesListView.kt`

**Adicionar:**
1. **Badge de status** em cada card
2. **Última entrega** (data relativa: "há 3 dias")
3. **Filtro por status** (dropdown)
4. **Contador de entregas** recebidas

**Layout sugerido para cada card:**
```
┌─────────────────────────────────────┐
│ Maria Santos        [Badge Status]   │
│ BEN-001 • Família: 4 pessoas        │
│                                      │
│ Última entrega: há 5 dias           │
│ 📦 12 entregas recebidas            │
│                                      │
│ [Ver Detalhes] [Editar]            │
└─────────────────────────────────────┘
```

### **Componente Novo**

**Arquivo:** `ui/admin/components/BeneficiaryStatusBadge.kt`

```kotlin
@Composable
fun BeneficiaryStatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (status) {
        "ACTIVE" -> "Ativo" to Color(0xFF66BB6A)
        "PENDING" -> "Pendente" to Color(0xFFFFA726)
        "INACTIVE" -> "Inativo" to Color(0xFF9E9E9E)
        else -> "Desconhecido" to Color.Gray
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.2f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
```

### **Calcular Última Entrega**

**No ViewModel:**
```kotlin
data class BeneficiaryWithStats(
    val beneficiary: Beneficiary,
    val lastDeliveryDate: Timestamp? = null,
    val totalDeliveries: Int = 0,
    val daysAgo: Int? = null  // dias desde última entrega
)

private suspend fun enrichBeneficiaryWithStats(
    beneficiary: Beneficiary
): BeneficiaryWithStats {
    var lastDelivery: Timestamp? = null
    var totalDeliveries = 0

    // Query última entrega
    deliveryRepository.getDeliveriesByBeneficiary(beneficiary.docId ?: "")
        .collect { result ->
            if (result is ResultWrapper.Success) {
                val deliveries = result.data ?: emptyList()
                totalDeliveries = deliveries.size
                lastDelivery = deliveries
                    .sortedByDescending { it.scheduledAt?.seconds }
                    .firstOrNull()?.scheduledAt
            }
        }

    val daysAgo = lastDelivery?.let {
        val now = System.currentTimeMillis()
        val deliveryMillis = it.toDate().time
        ((now - deliveryMillis) / (1000 * 60 * 60 * 24)).toInt()
    }

    return BeneficiaryWithStats(
        beneficiary = beneficiary,
        lastDeliveryDate = lastDelivery,
        totalDeliveries = totalDeliveries,
        daysAgo = daysAgo
    )
}
```

### **Formatação de Data Relativa**

```kotlin
fun formatRelativeTime(daysAgo: Int?): String {
    return when {
        daysAgo == null -> "Nunca"
        daysAgo == 0 -> "Hoje"
        daysAgo == 1 -> "Ontem"
        daysAgo < 7 -> "há $daysAgo dias"
        daysAgo < 30 -> "há ${daysAgo / 7} semanas"
        daysAgo < 365 -> "há ${daysAgo / 30} meses"
        else -> "há ${daysAgo / 365} anos"
    }
}
```

---

## 📦 Tarefa 3: Melhorar ProductsView

### **Objetivo**
Adicionar EANs, validades e alertas visuais aos produtos

### **Modificações na UI**

**Arquivo:** `ui/product/ProductsView.kt`

**Melhorias no ProductRow:**

```kotlin
@Composable
private fun ProductRow(
    product: Products,
    stockInfo: ProductStockInfo? = null,  // Novo
    onClick: () -> Unit
) {
    Card(...) {
        Column(Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name ?: "(Sem nome)",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "SKU: ${product.sku ?: "-"}",
                        style = MaterialTheme.typography.bodySmall
                    )

                    // NOVO: Categoria + Unidade
                    Text(
                        text = "${product.familia ?: "-"} • ${product.unidade ?: "-"}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }

                // NOVO: Badge de status
                stockInfo?.let { info ->
                    StockStatusBadge(
                        currentStock = info.currentStock,
                        minStock = info.minStock
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // NOVO: Stock visual
            stockInfo?.let { info ->
                StockProgressBar(
                    current = info.currentStock,
                    min = info.minStock
                )
            }

            // NOVO: EANs
            if (!product.eans.isNullOrBlank()) {
                Text(
                    text = "EAN: ${product.eans}",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // NOVO: Próximas validades
            stockInfo?.closestExpiry?.let { days ->
                ExpiryWarning(daysUntilExpiry = days)
            }
        }
    }
}
```

### **Novo Componente: StockProgressBar**

```kotlin
@Composable
fun StockProgressBar(
    current: Int,
    min: Int,
    modifier: Modifier = Modifier
) {
    val percentage = if (min > 0) {
        (current.toFloat() / (min * 2)) // 200% = completamente cheio
    } else 1f

    val color = when {
        current < min -> Color(0xFFF44336) // Vermelho
        current < min * 1.5 -> Color(0xFFFFA726) // Laranja
        else -> Color(0xFF2E7D32) // Verde
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Stock: $current",
                fontSize = 11.sp,
                color = Color.Gray
            )
            Text(
                text = "Mín: $min",
                fontSize = 11.sp,
                color = Color.Gray
            )
        }

        Spacer(Modifier.height(4.dp))

        LinearProgressIndicator(
            progress = percentage.coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = color,
            trackColor = Color.Gray.copy(alpha = 0.2f)
        )
    }
}
```

### **Novo Componente: ExpiryWarning**

```kotlin
@Composable
fun ExpiryWarning(
    daysUntilExpiry: Int,
    modifier: Modifier = Modifier
) {
    val (text, color) = when {
        daysUntilExpiry < 0 -> "⚠️ Expirado" to Color(0xFFF44336)
        daysUntilExpiry < 7 -> "⚠️ Expira em $daysUntilExpiry dias" to Color(0xFFF44336)
        daysUntilExpiry < 30 -> "⏰ Expira em $daysUntilExpiry dias" to Color(0xFFFFA726)
        else -> return
    }

    Text(
        text = text,
        fontSize = 11.sp,
        color = color,
        fontWeight = FontWeight.SemiBold,
        modifier = modifier
    )
}
```

### **ViewModel com Dados Agregados**

```kotlin
data class ProductStockInfo(
    val currentStock: Int,
    val minStock: Int,
    val closestExpiry: Int?,  // dias até expirar
    val activeLots: Int
)

data class ProductsListState(
    val products: List<Products> = emptyList(),
    val stockInfo: Map<String, ProductStockInfo> = emptyMap(),  // NOVO
    val showOnlyLowStock: Boolean = false,  // NOVO
    val isLoading: Boolean = false,
    val error: String? = null
)

// Carregar stock info para cada produto
private suspend fun loadStockInfo(productId: String): ProductStockInfo {
    var currentStock = 0
    var minStock = 0
    var closestExpiry: Int? = null
    var activeLots = 0

    // Query stock lots
    stockLotRepository.getStockLots(productId).collect { result ->
        if (result is ResultWrapper.Success) {
            val lots = result.data ?: emptyList()
            activeLots = lots.filter { it.remainingQty > 0 }.size
            currentStock = lots.sumOf { it.remainingQty }

            // Calcular próxima validade
            val now = System.currentTimeMillis()
            lots.forEach { lot ->
                lot.expiryDate?.let { timestamp ->
                    val days = ((timestamp.toDate().time - now) / (1000 * 60 * 60 * 24)).toInt()
                    if (closestExpiry == null || days < closestExpiry!!) {
                        closestExpiry = days
                    }
                }
            }
        }
    }

    return ProductStockInfo(
        currentStock = currentStock,
        minStock = minStock,
        closestExpiry = closestExpiry,
        activeLots = activeLots
    )
}
```

---

## 📊 Tarefa 4: Relatórios (Opcional - Média Prioridade)

### **Views a Criar**

**Arquivo:** `ui/admin/reports/ReportsView.kt`

**Relatórios disponíveis:**
1. **Resumo de Stock** - Entradas/saídas últimos 30 dias
2. **Doações por Doador** - Top doadores, volume, trimestre
3. **Entregas** - Realizadas e pendentes, mês atual
4. **Beneficiários Ativos** - Estatísticas de entregas

### **Exportação**

**Formato:** CSV (mais simples)

```kotlin
fun exportToCsv(data: List<Any>, filename: String) {
    val csvContent = buildString {
        // Headers
        append("Campo1,Campo2,Campo3\n")

        // Dados
        data.forEach { item ->
            append("$item\n")
        }
    }

    // Compartilhar via Intent
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/csv"
        putExtra(Intent.EXTRA_TEXT, csvContent)
        putExtra(Intent.EXTRA_TITLE, filename)
    }
    context.startActivity(Intent.createChooser(intent, "Exportar"))
}
```

---

## 🎯 Ordem de Implementação Recomendada

### **Sprint 1 (Prioritário)**
1. ✅ Stock Management UI (CONCLUÍDO)
2. ✅ Proteção de acesso User (CONCLUÍDO)
3. ⏳ **Adicionar status a Donations** (PRÓXIMO)
4. ⏳ Adicionar status a Beneficiaries

### **Sprint 2**
5. ⏳ Melhorar ProductsView (EANs, validades, alertas)
6. ⏳ Dashboard - Gráficos de tendências (opcional)

### **Sprint 3 (Futuro)**
7. ⏳ Relatórios com exportação CSV
8. ⏳ Notificações de alertas
9. ⏳ Sistema de agendamento avançado

---

## 📝 Notas Técnicas

### **Padrões a Seguir**
- **Cores de Status:**
  - Verde: #66BB6A (sucesso, ativo, OK)
  - Laranja: #FFA726 (aviso, pendente)
  - Vermelho: #F44336 (erro, crítico, expirado)
  - Azul: #42A5F5 (informação, processado)
  - Cinza: #9E9E9E (inativo, desabilitado)

- **Badges:**
  - `RoundedCornerShape(8.dp)`
  - Fundo: `color.copy(alpha = 0.2f)`
  - Padding: `horizontal = 12.dp, vertical = 6.dp`

- **Cards:**
  - `RoundedCornerShape(14.dp)`
  - Elevation: `1.dp` ou `2.dp`
  - Background: `Color.White` ou `Color(0xFFF6F6F6)`

### **Firebase Rules**
Já configuradas e funcionando. Não precisa alterar.

### **Testes**
Após cada implementação:
1. Testar como **Admin** (full access)
2. Testar como **User** (read-only)
3. Verificar permissões no Firestore
4. Testar com dados reais

---

## ✅ Checklist de Conclusão

### Donations com Status
- [ ] Modelo atualizado com `status` e `donationId`
- [ ] DonationStatusBadge criado
- [ ] Filtro por status funcionando
- [ ] ID sequencial gerado automaticamente
- [ ] Status atualiza quando processa doação
- [ ] UI atualizada com badges

### Beneficiaries com Status
- [ ] Modelo atualizado com `status` e `lastDeliveryDate`
- [ ] BeneficiaryStatusBadge criado
- [ ] Última entrega calculada e mostrada
- [ ] Contador de entregas funcionando
- [ ] Filtro por status funcionando
- [ ] UI atualizada com badges

### ProductsView Melhorado
- [ ] EANs visíveis na lista
- [ ] Validades próximas destacadas
- [ ] Badge de stock (baixo/OK)
- [ ] Barra de progresso de stock
- [ ] Filtro "apenas stock baixo"
- [ ] Agregação de dados funcionando

---

**Última atualização:** 2026-01-12
**Próxima revisão:** Após completar Sprint 1
