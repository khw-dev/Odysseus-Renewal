package com.example.ppet.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object PhoneUtils {
    fun makePhoneCall(context: Context, phoneNumber: String) {
        try {
            val cleanPhoneNumber = phoneNumber.replace(Regex("[^0-9]"), "")

            if (cleanPhoneNumber.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$cleanPhoneNumber")
                }
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "유효하지 않은 전화번호입니다", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "전화 앱을 열 수 없습니다", Toast.LENGTH_SHORT).show()
        }
    }
}
