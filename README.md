# 🛒 LojaSocial - Sistema de Gestão de Loja Social

![Android](https://img.shields.io/badge/Android-7.0%2B-3DDC84?logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9-7F52FF?logo=kotlin)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?logo=firebase)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?logo=jetpackcompose)

Sistema Android completo para gestão de lojas sociais, desenvolvido em Kotlin com Jetpack Compose. Permite gerir doações, entregas, stock com rastreabilidade FIFO, beneficiários e produtos de forma eficiente e intuitiva.

---

## 📑 Índice

- [Sobre o Projeto](#-sobre-o-projeto)
- [Funcionalidades Principais](#-funcionalidades-principais)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Requisitos](#-requisitos)
- [Como Instalar](#-como-instalar)
- [Como Utilizar](#-como-utilizar)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Funcionalidades Futuras](#-funcionalidades-futuras)
- [Contribuir](#-contribuir)

---

## 📖 Sobre o Projeto

A **LojaSocial** é uma aplicação móvel Android desenvolvida para facilitar a gestão do SAS do ipca - estabelecimento que distribue produtos alimentares e de primeira necessidade a estudantes em situação de vulnerabilidade social.

### O que é uma Loja Social?

Uma loja social é um espaço onde beneficiários (estudantes, famílias carenciadas) podem receber produtos gratuitamente, provenientes de doações de empresas e particulares.

### Propósito

Digitalizar e otimizar todo o processo de gestão, desde o registo de doações até à entrega aos beneficiários, garantindo:
- ✅ Rastreabilidade total (de onde veio cada produto e para onde foi)
- ✅ Controlo de validades (FIFO - First In, First Out)
- ✅ Alertas automáticos de stock baixo
- ✅ Histórico completo de movimentos

### Público-Alvo

- 🏫 SAS IPCA (gabinete de ação social)


---

## ⚡ Funcionalidades Principais

### 👤 Gestão de Utilizadores
- **Autenticação segura** via Firebase (email/password)
- **Dois níveis de acesso**:
  - 👨‍💼 **Administrador**: acesso total a todas as funcionalidades
  - 👤 **Utilizador**: visualização de produtos (modo leitura)
- Perfis de utilizador personalizados

### 📦 Gestão de Artigos/Produtos
- Registo completo de produtos
- **Scanner de código de barras** integrado (EAN/UPC)
- Controlo de **stock mínimo** e **stock atual**
- Localização física (prateleira)
- Categorização por famílias
- Modo de leitura para utilizadores não-admin

### 🎁 Gestão de Doações
- Registo de doações com identificação do doador
- **IDs sequenciais automáticos** (DOA-001, DOA-002, ...)
- **Estados de doação** com badges visuais:
  - 🟠 **Triagem** (PENDING) - aguarda processamento
  - 🟢 **Recebida** (RECEIVED) - produtos já no stock
  - 🔵 **Processada** (PROCESSED) - totalmente processada
- Linhas de doação com **datas de validade**
- **Filtros** por estado e doador
- Entrada **automática de stock** (movimentos IN)

### 📊 Gestão de Stock
Sistema completo com rastreabilidade total:

**Lotes de Stock (StockLot)**:
- Número de lote único
- Quantidade inicial vs. restante
- **Data de validade** (Timestamp)
- Origem (doador)

**Alertas Automáticos**:
- ⚠️ **Validade próxima**: expira em menos de 30 dias
- 🚨 **Validade crítica**: expira em menos de 7 dias
- ❌ **Expirado**: já passou da validade
- 📉 **Stock baixo**: abaixo do mínimo definido

**Visualizações**:
- 📋 **Overview geral**: lista todos os artigos com alertas
- 📦 **Lotes por produto**: detalhe de cada lote com validades
- 📈 **Histórico de movimentos**: registo completo IN/OUT

**Pesquisa e Filtros**:
- Por nome, SKU ou EAN
- Apenas stock baixo
- Por tipo de movimento (entrada/saída)

### 👥 Gestão de Beneficiários
- Registo completo (nome, nº aluno, NIF, contactos, curso, ano)
- **Estados de beneficiário**:
  - 🟢 **Ativo** (ACTIVE)
  - 🟠 **Pendente** (PENDING)
  - ⚫ **Inativo** (INACTIVE)
- **Histórico de entregas**:
  - Última entrega com tempo relativo ("Há 3 dias", "Há 2 semanas")
  - Total de entregas realizadas
- Filtros por estado
- Pesquisa por nome ou nº aluno

### 🚚 Gestão de Entregas
Sistema inteligente com consumo automático FIFO:

- Planeamento de entregas para beneficiários
- Seleção de produtos e quantidades
- **Consumo automático FIFO** (First In, First Out):
  - Sistema escolhe automaticamente os lotes mais antigos
  - **Previne desperdício** por validade
  - Otimiza rotação de stock
- **Estados de entrega**:
  - 🟠 **Planeada** (SCHEDULED)
  - 🟢 **Entregue** (DELIVERED)
- **Rastreabilidade total**: saber exatamente qual lote foi entregue a quem
- Atualização automática de stock (movimentos OUT)
- Filtros por estado e beneficiário

### 🏢 Gestão de Doadores
- Registo de doadores (empresas e particulares)
- **Tipos**: COMPANY ou PRIVATE
- Informação: nome, email, NIF
- Histórico de doações por doador

### 📊 Dashboard Administrativo
Visão geral em tempo real:

**Indicadores**:
- 📦 Total de artigos (com nº em alerta)
- 🎁 Total de doações
- 👥 Total de beneficiários ativos
- 🚚 Total de entregas realizadas

**Alertas Visuais**:
- ⚠️ Stock baixo - produtos abaixo do mínimo
- 📅 Validades próximas - lotes a expirar em breve

**Ações Rápidas**:
- Acesso direto à gestão de stock
- Acesso direto ao histórico de doações

---

## 🛠️ Tecnologias Utilizadas

### Linguagem e Framework
- **Kotlin** - Linguagem moderna e segura
- **Jetpack Compose** - UI declarativa moderna
- **Material Design 3** - Sistema de design Google

### Arquitetura
- **MVVM** (Model-View-ViewModel)
- **Clean Architecture** - Separação clara de camadas
- **Dependency Injection** com Hilt/Dagger
- **Kotlin Coroutines** + **Flow** - Programação assíncrona reativa
- **Navigation Compose** - Navegação entre ecrãs

### Backend e Serviços
- **Firebase Authentication** - Autenticação segura
- **Cloud Firestore** - Base de dados NoSQL em tempo real
- **Firebase Storage** - (preparado para futuro)

### Bibliotecas Principais
| Biblioteca | Propósito |
|------------|-----------|
| **CameraX** | Captura de câmara |
| **ML Kit Barcode Scanning** | Leitura de códigos de barras |
| **Coil** | Carregamento de imagens |
| **Hilt** | Injeção de dependências |

---

## 📋 Requisitos

### Sistema
- ✅ **Android 7.0 (Nougat)** ou superior (API 24+)
- ✅ **Câmara** (para scanner de códigos)
- ✅ **Ligação à Internet** (para sincronização Firebase)

### Desenvolvimento
- ✅ **Android Studio** Hedgehog ou superior
- ✅ **JDK 11** ou superior
- ✅ **Gradle 8.x**
- ✅ **Conta Firebase** configurada

---

## 🚀 Como Instalar

### 1️⃣ Configuração do Firebase

#### Criar Projeto Firebase
1. Aceder ao [Firebase Console](https://console.firebase.google.com/)
2. Criar novo projeto ou usar existente
3. Adicionar aplicação Android:
   - Package name: `com.example.lojasocial`
   - Descarregar `google-services.json`
   - Colocar na pasta `app/`

#### Ativar Serviços
1. **Authentication**:
   - Build → Authentication → Sign-in method
   - Ativar **Email/Password**

2. **Firestore Database**:
   - Build → Firestore Database → Create database
   - Escolher modo de produção
   - Selecionar localização (europe-west1 recomendado)

3. **Adicionar SHA-1** (para reCAPTCHA):
   ```bash
   # Windows
   cd android
   gradlew signingReport

   # Mac/Linux
   ./gradlew signingReport
   ```
   - Copiar SHA-1 do resultado
   - Project Settings → Android apps → Add fingerprint

### 2️⃣ Regras de Segurança Firestore

Criar/atualizar o ficheiro `firestore.rules` na raiz do projeto:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    // Funções auxiliares
    function isSignedIn() {
      return request.auth != null;
    }

    function isAdmin() {
      return isSignedIn() &&
             exists(/databases/$(database)/documents/users/$(request.auth.uid)) &&
             get(/databases/$(database)/documents/users/$(request.auth.uid)).data.userType == 'admin';
    }

    // Artigos: todos podem ler, só admin escreve
    match /items/{itemId} {
      allow read: if isSignedIn();
      allow write: if isAdmin();

      match /stockLots/{lotId} {
        allow read: if isSignedIn();
        allow write: if isAdmin();
      }
    }

    // Só admins acedem
    match /beneficiaries/{beneficiaryId} {
      allow read, write: if isAdmin();
    }

    match /donations/{donationId} {
      allow read, write: if isAdmin();

      match /lines/{lineId} {
        allow read, write: if isAdmin();
      }
    }

    match /deliveries/{deliveryId} {
      allow read, write: if isAdmin();

      match /lines/{lineId} {
        allow read, write: if isAdmin();
      }
    }

    match /donors/{donorId} {
      allow read, write: if isAdmin();
    }

    match /stockMoves/{moveId} {
      allow read: if isSignedIn();
      allow write: if isAdmin();
    }

    // Utilizadores: só podem ler o próprio perfil
    match /users/{userId} {
      allow read: if isSignedIn() && request.auth.uid == userId;
      allow write: if isAdmin();
    }

    // Contadores (para IDs sequenciais)
    match /counters/{counterId} {
      allow read: if isSignedIn();
      allow write: if isAdmin();
    }
  }
}
```

Publicar no Firebase Console:
- Firestore Database → Rules → copiar conteúdo → Publish

### 3️⃣ Inicializar Contador de Doações

No Firebase Console → Firestore Database:
1. Criar collection `counters`
2. Criar documento com ID `donations`
3. Adicionar campo:
   - Nome: `count`
   - Tipo: number
   - Valor: `0`

### 4️⃣ Criar Utilizador Administrador

1. **Registar na app**:
   - Abrir app → Registar
   - Inserir email e password
   - Confirmar registo

2. **Promover a admin** (no Firestore):
   - Ir a `users/{userId}` (usar o UID do Authentication)
   - Adicionar campo:
     - Nome: `userType`
     - Tipo: string
     - Valor: `"admin"`

### 5️⃣ Clonar e Compilar

```bash
# Clonar repositório
git clone <url-do-repositório>
cd LojaSocial

# Abrir no Android Studio
# File → Open → selecionar pasta LojaSocial

# Adicionar google-services.json
# (copiar para app/)

# Sincronizar Gradle
# Toolbar: File → Sync Project with Gradle Files

# Executar
# Toolbar: Run → Run 'app'
# ou tecla Shift+F10
```

---

## 💡 Como Utilizar

### 🔐 Primeiro Acesso

1. **Registar conta**:
   - Abrir app
   - Clicar em "Registar"
   - Preencher email e password
   - Confirmar

2. **Aguardar aprovação**:
   - Um administrador precisa de definir `userType: "admin"` no Firestore
   - Ou seguir passos da secção [4️⃣ Criar Utilizador Administrador](#4️⃣-criar-utilizador-administrador)

3. **Fazer login**:
   - Email e password
   - Acesso conforme permissões

### 👤 Utilizador Normal

Pode apenas:
- ✅ Ver lista de produtos
- ✅ Ver detalhes de produtos
- ❌ **Não pode** criar/editar/eliminar

### 👨‍💼 Administrador

Acesso total via **bottom navigation bar**:

#### 🏠 **Início (Dashboard)**
- Estatísticas gerais
- Alertas de stock baixo
- Alertas de validades próximas
- Ações rápidas

#### 👥 **Beneficiários**
- Listar todos os beneficiários
- Criar novo beneficiário
- Editar informações
- Ver histórico de entregas
- Filtrar por estado (Ativo/Pendente/Inativo)

#### 🎁 **Doações**
- Registar nova doação
- Selecionar doador
- Adicionar linhas com produtos e validades
- Acompanhar estados (Triagem → Recebida → Processada)
- Filtrar por estado

#### 📦 **Artigos**
- Listar produtos
- Criar novo produto
- Editar produto
- Scanner de código de barras (📷)
- Ver stock atual vs. mínimo
- Ver alertas de stock baixo

#### 🚚 **Entregas**
- Planear nova entrega
- Selecionar beneficiário
- Escolher produtos (sistema mostra stock disponível)
- **Sistema consome automaticamente stock FIFO**
- Marcar como entregue

#### ⋮ **Mais**
- **Gestão de Stock**:
  - Overview geral (todos os artigos)
  - Lotes por produto (detalhe com validades)
  - Histórico de movimentos (entrada/saída)
- Gestão de Doadores
- Perfil do utilizador
- Logout

---

### 🔄 Fluxo Típico: Doação → Entrega

#### 1️⃣ Registar Doação
```
Doações → [+] → Selecionar Doador
→ Adicionar produtos (item + quantidade + validade)
→ Guardar
✅ Sistema gera ID automático (DOA-001)
✅ Stock aumenta automaticamente
✅ Cria lotes com validades
✅ Regista movimento IN
```

#### 2️⃣ Processar Doação
```
Doações → Selecionar doação
→ Alterar estado para "Recebida"
✅ Produtos ficam disponíveis no stock
```

#### 3️⃣ Planear Entrega
```
Entregas → [+] → Selecionar Beneficiário
→ Adicionar produtos
→ Escolher data
→ Guardar
✅ Sistema consome dos lotes mais antigos (FIFO)
✅ Regista movimento OUT
✅ Stock diminui automaticamente
✅ Atualiza última entrega do beneficiário
```

#### 4️⃣ Concluir Entrega
```
Entregas → Selecionar entrega
→ Alterar estado para "Entregue"
✅ Entrega completa
✅ Histórico atualizado
```

---

## 📂 Estrutura do Projeto

```
LojaSocial/
├── app/
│   ├── src/main/java/com/example/lojasocial/
│   │   │
│   │   ├── 📁 models/                    # Modelos de dados
│   │   │   ├── Beneficiary.kt            # Beneficiário com status
│   │   │   ├── Delivery.kt               # Entrega
│   │   │   ├── DeliveryLine.kt           # Linha de entrega
│   │   │   ├── Donation.kt               # Doação com status e ID sequencial
│   │   │   ├── DonationLine.kt           # Linha de doação
│   │   │   ├── Donor.kt                  # Doador
│   │   │   ├── Item.kt                   # Produto/artigo
│   │   │   ├── StockLot.kt               # Lote com validade
│   │   │   ├── StockMove.kt              # Movimento (IN/OUT)
│   │   │   ├── User.kt                   # Utilizador
│   │   │   └── ResultWrapper.kt          # Wrapper para respostas
│   │   │
│   │   ├── 📁 data/repository/           # Repositórios (acesso dados)
│   │   │   ├── BeneficiaryRepository.kt
│   │   │   ├── DeliveryRepository.kt
│   │   │   ├── DonationRepository.kt
│   │   │   ├── DonorRepository.kt
│   │   │   ├── ItemRepository.kt
│   │   │   ├── StockLotRepository.kt
│   │   │   └── StockMoveRepository.kt
│   │   │
│   │   ├── 📁 ui/                        # Interface
│   │   │   │
│   │   │   ├── 📁 admin/                 # Área administrativa
│   │   │   │   ├── AdminStartView.kt     # Dashboard
│   │   │   │   ├── AdminStartViewModel.kt
│   │   │   │   │
│   │   │   │   ├── 📁 components/        # Componentes reutilizáveis
│   │   │   │   │   ├── AdminBottomBar.kt
│   │   │   │   │   ├── BeneficiaryStatusBadge.kt
│   │   │   │   │   ├── DonationStatusBadge.kt
│   │   │   │   │   └── StatsCard.kt
│   │   │   │   │
│   │   │   │   ├── 📁 donations/         # Gestão doações
│   │   │   │   ├── 📁 deliveries/        # Gestão entregas
│   │   │   │   └── 📁 stock/             # Gestão stock
│   │   │   │       ├── StockOverviewView.kt
│   │   │   │       ├── StockLotsView.kt
│   │   │   │       └── StockMovesView.kt
│   │   │   │
│   │   │   ├── 📁 Beneficiary/           # Beneficiários
│   │   │   ├── 📁 donor/                 # Doadores
│   │   │   ├── 📁 product/               # Produtos
│   │   │   ├── 📁 login/                 # Login
│   │   │   ├── 📁 register/              # Registo
│   │   │   ├── 📁 perfil/                # Perfil
│   │   │   └── 📁 BarcodeScanner/        # Scanner
│   │   │
│   │   ├── 📁 navigation/                # Navegação
│   │   │   ├── AdminNavGraph.kt
│   │   │   └── UserNavGraph.kt
│   │   │
│   │   ├── AppModule.kt                  # DI (Hilt)
│   │   ├── Constants.kt                  # Constantes
│   │   └── MainActivity.kt               # Activity principal
│   │
│   ├── google-services.json              # Config Firebase
│   └── build.gradle.kts
│
├── firestore.rules                       # Regras segurança
└── README.md                             # Este ficheiro
```

---

## 🚧 Funcionalidades Futuras

### Em Desenvolvimento
- 📊 **Relatórios exportáveis** (CSV/PDF)
  - Resumo de stock (entradas/saídas)
  - Doações por doador
  - Entregas por período
- 📈 **Gráficos estatísticos** no dashboard
  - Evolução de doações (últimos 6 meses)
  - Evolução de entregas (últimos 6 meses)

### Planeadas
- 🔔 **Notificações push**
  - Alertas de validade a expirar
  - Alertas de stock baixo
  - Entregas planeadas para hoje
- 📱 **Modo offline**
  - Sincronização quando recupera conexão
  - Cache local de dados essenciais
- 🖨️ **Impressão de etiquetas**
  - Códigos de barras para produtos
  - Etiquetas de lotes com validade
  - QR codes para rastreabilidade
- 👨‍👩‍👧‍👦 **Gestão de famílias**
  - Agrupamento de beneficiários por família
  - Entregas por agregado familiar
- 📅 **Sistema de agendamento avançado**
  - Calendário visual de entregas
  - Lembretes automáticos
  - Conflitos de horário

---

## 🤝 Contribuir

Este é um projeto académico desenvolvido por estudantes do 3º ano. Contribuições são bem-vindas!

### Como Contribuir

1. **Fork** o projeto
2. Criar branch para feature:
   ```bash
   git checkout -b feature/NovaFuncionalidade
   ```
3. Commit das alterações:
   ```bash
   git commit -m 'Adiciona nova funcionalidade X'
   ```
4. Push para o branch:
   ```bash
   git push origin feature/NovaFuncionalidade
   ```
5. Abrir **Pull Request**

### Diretrizes

- Seguir padrões de código existentes
- Adicionar comentários em código complexo
- Testar alterações antes de submeter
- Atualizar documentação se necessário

---

## 👥 Equipa

Desenvolvido por estudantes do 3º ano como projeto académico.

---

## 📄 Licença

Projeto educacional - Todos os direitos reservados à instituição de ensino.

---

## 💬 Suporte

Para questões, problemas ou sugestões:
- 🐛 [Abrir issue](../../issues) no repositório
- 📧 Contactar a equipa de desenvolvimento

---

## 📝 Observações Técnicas

### Padrões de Código
- **Clean Architecture** com separação clara de camadas
- **SOLID principles** aplicados consistentemente
- **Repository Pattern** para acesso a dados
- **State Management** com StateFlow
- **Dependency Injection** com Hilt

### Segurança
- ✅ Regras de segurança Firestore aplicadas
- ✅ Validação de permissões em todos os endpoints
- ✅ Autenticação obrigatória
- ✅ Separação clara admin/utilizador
- ✅ Queries filtradas por permissões

### Performance
- ✅ Queries otimizadas com índices Firestore
- ✅ Carregamento lazy de listas (LazyColumn)
- ✅ Cache de imagens com Coil
- ✅ Flows reativos para UI sempre atualizada
- ✅ Paginação em listas longas

---

<div align="center">

**Versão**: 1.0.0
**Última atualização**: Janeiro 2026
**Estado**: ✅ Em desenvolvimento ativo

---

Desenvolvido com ❤️ pelo estudantes Antonio Ferreira(9657), Gonçalo Gomes(23039), Ruben Dias(23033), Mafalda Barrao(20446), Joao Marcelo(23041)

</div>
