# Elevate - Aplikasi Pembelajaran Online

## Implementasi Font Poppins

Aplikasi Elevate menggunakan font Poppins sebagai font utama di seluruh aplikasi. Berikut ini implementasi font Poppins:

### 1. Konfigurasi Font

Font Poppins diimplementasikan melalui beberapa mekanisme utama:

- **File font** - Font Poppins (Regular, Medium, SemiBold, Bold, ExtraBold) tersedia di direktori `app/src/main/res/font/`
- **Type.kt** - Mendefinisikan `PoppinsFontFamily` dan mengonfigurasi `Typography` aplikasi dengan font Poppins
- **Theme.kt** - Menggunakan `CompositionLocalProvider` untuk menyediakan `LocalTextStyle` dengan font Poppins dan mengaplikasikan `Typography` ke `MaterialTheme`

### 2. Penggunaan Font

Dengan konfigurasi di atas, semua komponen Text di aplikasi akan menggunakan font Poppins secara otomatis melalui:

1. **MaterialTheme Typography** - Komponen dengan style dari MaterialTheme.typography
2. **LocalTextStyle Provider** - Komponen Text tanpa style eksplisit
3. **TextField** dan **OutlinedTextField** dengan textStyle = LocalTextStyle.current

### 3. Dokumentasi Penggunaan

Untuk informasi lebih detail tentang implementasi font dan praktik terbaik penggunaan font Poppins, lihat `docs/PANDUAN_FONT.md`.