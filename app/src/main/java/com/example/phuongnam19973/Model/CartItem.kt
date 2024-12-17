package com.example.phuongnam19973.Model

import android.os.Parcel
import android.os.Parcelable

data class CartItem(
    val id: String = "",
    val name: String = "",
    val price: Double = 0.0,
    var quantity: Int = 0,
    val imageUrl: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "",
        name = parcel.readString() ?: "",
        price = parcel.readDouble(),
        quantity = parcel.readInt(),
        imageUrl = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeDouble(price)
        parcel.writeInt(quantity)
        parcel.writeString(imageUrl)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<CartItem> {
        override fun createFromParcel(parcel: Parcel): CartItem = CartItem(parcel)
        override fun newArray(size: Int): Array<CartItem?> = arrayOfNulls(size)
    }
}
