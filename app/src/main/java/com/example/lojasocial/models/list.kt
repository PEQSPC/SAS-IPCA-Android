package com.example.lojasocial.models

import org.json.JSONObject


data class Product (
    var id : Int? = null,
    var title : String? = null,
    var category : String? = null,
    var description : String? = null,
    var price : Double? = null,
    var discountPercentage : Double? = null,
    var rating : Double? = null,
    var stock : Int? = null,
    var tags : List<String>? = null,
    var brand : String? = null,
    var sku : String? = null,
    var weight : Double? = null,
    var dimensions : List<String>? =null,
    var warrantyInformation : String? = null,
    var shippingInformation : String? = null,
    var availabilityStatus : String? = null,
    var reviews : List<String>? = null,
    var returnPolicy : String? = null,
    var minimumOrderQuantity : Int? = null,
    var meta : List<String>? = null,
    var images : String? = null,
    var thumbnail : String? = null

){
    companion object {
        fun fromJson(jsonObject: JSONObject): Product {

            var tags = jsonObject.getJSONArray("tags")
            var tagsList = arrayListOf<String>()
            for (index in 0..<tags.length()) {
                tagsList.add(tags.getString(index))
            }

            val reviews = jsonObject.getJSONArray("reviews")
            val reviewsList = arrayListOf<String>()
            for (index in 0..<reviews.length()) {
                reviewsList.add(reviews.getString(index))
            }


            return Product(
                id = jsonObject.getInt("id"),
                title = jsonObject.getString("title") ,
                category = jsonObject.getString("category"),
                description = jsonObject.getString("description"),
                price = jsonObject.getDouble("price"),
                discountPercentage = jsonObject.getDouble("discountPercentage"),
                rating = jsonObject.getDouble("rating"),
                stock = jsonObject.getInt("stock"),
                //brand =  jsonObject.getString("brand"),
                sku = jsonObject.getString("sku"),
                weight = jsonObject.getDouble("weight"),
                warrantyInformation = jsonObject.getString("warrantyInformation"),
                shippingInformation = jsonObject.getString("shippingInformation"),
                availabilityStatus = jsonObject.getString("availabilityStatus"),
                returnPolicy = jsonObject.getString("returnPolicy"),
                minimumOrderQuantity = jsonObject.getInt("minimumOrderQuantity"),
                images = jsonObject.getString("images"),
                thumbnail = jsonObject.getString("thumbnail"),
                tags = tagsList,
                //meta = metaList,
                reviews = reviewsList

            )
        }
    }
}