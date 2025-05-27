# Implementasi Kategori Kursus

## Pendekatan
Pada implementasi ini, kami menggunakan model dan komponen yang sudah ada daripada membuat model dan komponen baru. Ini meningkatkan efisiensi kode dan konsistensi aplikasi.

## Struktur
1. **CategoryScreen**: Halaman utama yang menampilkan grid kategori yang tersedia
2. **CategoryCoursesScreen**: Halaman yang menampilkan daftar kursus berdasarkan kategori yang dipilih
3. **CategoryDetailCourseCard**: Komponen khusus untuk menampilkan kartu kursus dengan fungsi klik

## Alur
1. User melihat CategoryScreen dengan grid kategori
2. User mengklik salah satu kategori
3. Aplikasi mengarahkan ke CategoryCoursesScreen dengan ID kategori sebagai parameter
4. CategoryCoursesScreen menampilkan daftar kursus dari kategori tersebut
5. User dapat mencari kursus di kategori tersebut
6. User dapat mengklik kursus untuk melihat detail kursus

## Keuntungan Pendekatan Ini
1. Penggunaan kembali model `Course` yang sudah ada
2. Tidak perlu membuat model baru yang redundan
3. Kode lebih terorganisir dan mudah dipelihara
4. Meningkatkan konsistensi UI/UX 