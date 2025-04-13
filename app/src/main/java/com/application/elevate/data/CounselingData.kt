package com.application.elevate.data
import com.application.elevate.model.Consultant
import com.application.elevate.model.PaymentDetail
import com.application.elevate.model.PaymentMethod
import com.application.elevate.R

object CounselingData {
    fun getConsultants(category: String = ""): List<Consultant> = listOf(
        Consultant("1","Barbie S.Ds.","UI/UX Consultant",4.0f,191,25000,R.drawable.cv_review),
        Consultant("2","Ken S.Kom.","UI/UX Designer",5.0f,204,30000,R.drawable.counseling),
        Consultant("3","Alan S.Ds.","Graphic Designer",5.0f,178,40000,R.drawable.counseling)
    )

    fun searchConsultants(q: String): List<Consultant> =
        getConsultants().filter { it.name.contains(q, ignoreCase = true) }

    fun getPaymentDetail(): PaymentDetail = PaymentDetail(
        consultationFee = 29999,
        discount = 4999,
        serviceCharge = 1000,
        total = 26000,
        methods = listOf(
//            PaymentMethod("gopay","Gopay", R.drawable.ic_gopay),
//            PaymentMethod("dana","DANA", R.drawable.ic_dana),
//            PaymentMethod("shopeepay","ShopeePay", R.drawable.ic_shopeepay),
//            PaymentMethod("ovo","OVO", R.drawable.ic_ovo)
        )
    )
}