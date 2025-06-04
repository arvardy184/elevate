# Panduan Implementasi Font Poppins di Aplikasi Elevate

Aplikasi Elevate menggunakan font Poppins di seluruh UI aplikasi. Berikut adalah panduan bagaimana font Poppins diimplementasikan dan cara menggunakannya.

## Konfigurasi Font

Font Poppins sudah dikonfigurasi di `Type.kt` sebagai font default untuk Typography aplikasi:

```kotlin
// Font Poppins untuk seluruh aplikasi
val PoppinsFontFamily = FontFamily(
    Font(R.font.poppins_regular, FontWeight.Normal),
    Font(R.font.poppins_medium, FontWeight.Medium),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold),
    Font(R.font.poppins_extrabold, FontWeight.ExtraBold)
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = PoppinsFontFamily,
        // ...
    ),
    // ... komponen typography lainnya
)
```

Kemudian Typography ini diterapkan ke MaterialTheme di `Theme.kt`. Selain itu, kita menerapkan font Poppins untuk semua komponen Text secara otomatis dengan CompositionLocalProvider:

```kotlin
// Di dalam ReplyTheme
CompositionLocalProvider(
    LocalTextStyle provides LocalTextStyle.current.copy(fontFamily = PoppinsFontFamily)
) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
```

## Penerapan Font Poppins Secara Otomatis

Dengan pendekatan di atas, kita memastikan semua komponen Text dalam aplikasi secara otomatis menggunakan font Poppins tanpa perlu mengubah masing-masing komponen:

1. **Typography** - Semua style MaterialTheme.typography.* dikonfigurasi menggunakan PoppinsFontFamily
2. **LocalTextStyle** - CompositionLocalProvider memastikan komponen Text yang tidak secara eksplisit menetapkan style tetap menggunakan font Poppins

Ini berarti bahkan komponen Text dasar seperti:

```kotlin
Text("Hello World")
```

akan secara otomatis menggunakan font Poppins.

## Menggunakan Font Poppins

Dengan konfigurasi di atas, semua komponen Text yang menggunakan `MaterialTheme.typography.*` akan otomatis menggunakan font Poppins. 

### Cara Terbaik Mengimplementasikan Font

1. **Gunakan MaterialTheme.typography**

   ```kotlin
   Text(
       text = "Contoh Teks",
       style = MaterialTheme.typography.bodyLarge
   )
   ```

2. **Jika perlu menyesuaikan style, gunakan copy dengan mempertahankan fontFamily**

   ```kotlin
   Text(
       text = "Contoh Teks",
       style = MaterialTheme.typography.bodyLarge.copy(
           fontSize = 18.sp,
           fontWeight = FontWeight.Bold
       )
   )
   ```

3. **Untuk kasus khusus, gunakan PoppinsFontFamily secara eksplisit**

   ```kotlin
   Text(
       text = "Contoh Teks",
       style = TextStyle(
           fontFamily = PoppinsFontFamily,
           fontSize = 16.sp,
           fontWeight = FontWeight.Medium
       )
   )
   ```

## Tips Untuk Konsistensi UI

- Hindari menggunakan fontFamily selain Poppins, kecuali ada kebutuhan desain yang sangat spesifik
- Pastikan semua komponen Text menggunakan `style = MaterialTheme.typography.*` atau setidaknya `fontFamily = PoppinsFontFamily` 
- Untuk TextField dan OutlinedTextField, yang menerima parameter textStyle, gunakan:
  ```kotlin
  textStyle = LocalTextStyle.current
  ```
  atau jika ingin kustomisasi:
  ```kotlin
  textStyle = LocalTextStyle.current.copy(fontSize = 14.sp)
  ```
  Font Poppins akan otomatis diterapkan melalui LocalTextStyle provider

## Font Poppins di Custom Component

Jika membuat komponen kustom yang berisi teks, pastikan untuk:

1. Menggunakan Typography yang disediakan MaterialTheme, atau
2. Gunakan LocalTextStyle.current yang sudah dikonfigurasi dengan PoppinsFontFamily

```kotlin
@Composable
fun MyCustomComponent(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium.copy(
            // Modifikasi lainnya...
        )
    )
}
```

Dengan mengikuti panduan ini, aplikasi Elevate akan memiliki tampilan font yang konsisten di seluruh UI. 