package com.example.lojasocial

object AppConstants {

    const val loginRoute = "login"
    const val startPage = "Inicio"
    const val registerRoute = "Registo"

    const val home = "home"
    const val adminHome = "adminHome"

    const val products = "products"
    const val productsDetail = "productsDetail/{docId}"
    const val productsDetailCreate = "productsDetail"

    const val beneficiaries = "beneficiaries"
    const val createBeneficiary = "beneficiaries/create"
    const val beneficiaryDetail = "beneficiaries/detail/{docId}"
    const val families = "families"
    const val createFamily = "families/create"
    const val familyDetail = "families/detail/{docId}"

    const val profile = "profile"

    const val agendas = "agendas"
    const val agendasCreate = "agendas/create"
    const val agendasDetail = "agendas/detail/{docId}"

    const val donors = "donors"
    const val createDonor = "donors/create"
    const val donorDetail = "donors/detail/{docId}"

    const val donations = "donations"
    const val createDonation = "donations/create"
    const val donationDetail = "donations/detail/{docId}"

    const val deliveries = "deliveries"
    const val createDelivery = "deliveries/create"
    const val deliveryDetail = "deliveries/detail/{docId}"

    const val stock = "stock"
    const val stockLots = "stock/lots/{itemId}"
    const val stockMoves = "stock/moves"

    const val carts = "carts"
}