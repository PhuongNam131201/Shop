package com.example.phuongnam19973.Model

import android.os.Parcel
import android.os.Parcelable

data class Order(
    val id: String = "", // Thêm thuộc tính id
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val address: String = "",
    val content: String = "",
    val phoneNumber: String = "",
    var status: String = "Chờ xác nhận"
) : Parcelable {
    constructor(parcel: Parcel) : this(
        id = parcel.readString() ?: "", // Đọc id từ Parcel
        userId = parcel.readString() ?: "",
        items = mutableListOf<CartItem>().apply {
            parcel.readTypedList(this, CartItem.CREATOR)
        },
        totalAmount = parcel.readDouble(),
        address = parcel.readString() ?: "",
        content = parcel.readString() ?: "",
        phoneNumber = parcel.readString() ?: "",
        status = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id) // Ghi id vào Parcel
        parcel.writeString(userId)
        parcel.writeTypedList(items)
        parcel.writeDouble(totalAmount)
        parcel.writeString(address)
        parcel.writeString(content)
        parcel.writeString(phoneNumber)
        parcel.writeString(status)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<Order> {
        override fun createFromParcel(parcel: Parcel): Order = Order(parcel)
        override fun newArray(size: Int): Array<Order?> = arrayOfNulls(size)
    }
}
