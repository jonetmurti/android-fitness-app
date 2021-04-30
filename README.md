# Workout App
1. Yahya - 13518029
2. Jonet Wira Murti - 13518083
3. Felicia Gojali - 13518101

## Deskripsi Aplikasi
Workout App adalah aplikasi dalam bidang kesehatan ataupun olahraga yang dapat membantu pengguna dalam melakukan tracking serta scheduling kegiatan olahraga yang dilakukannya. Selain itu, aplikasi ini juga dapat menampilkan berita-berita terkait olahraga terbaru yang dapat dilihat pengguna untuk menambah pengetahuan seputar olahraga.

## Cara Kerja
Ketika pengguna membuka aplikasi, pengguna akan ditampilkan Sport News. Kemudian, pengguna dapat melihat fitur yang ada dengan menekan "hamburger" icon.
Terdapat beberapa menu yang dapat dipilih :
1. News
2. Walking
3. Cycling
4. History
5. Scheduler

### News
Pada menu ini, pengguna akan ditampilkan berita - berita yang diambil dari Sport News API. Pengguna juga dapat melihat berita tersebut dengan klik salah satu berita yang terdapat pada list. Ketika item diklik, akan ditampilkan WebView yang akan menampilkan halaman web yang menuju ke berita tersebut.

### Walking
Pada menu ini, pengguna dapat melakukan tracking latihan apabila ingin berjalan/berlari seperti jogging. Aplikasi ini akan mendeteksi jumlah langkah pengguna dan menampilkannya di layar. Menu ini juga dapat berjalan di background, jadi apabila pengguna keluar dari aplikasi, pengguna akan tetap dapat melakukan tracking dan notifikasi akan muncul di smartphone pengguna yang akan menampilkan jumlah langkah pengguna yang terdeteksi.

Ketika pengguna selesai, pengguna akan ditampilkan tampilan informasi latihan yang telah dilakukan, serta akan ditampilkan jumlah langkah dari training yang telah dilakukan. Dan juga informasi training tersebut akan disimpan pada history.

### Cycling
Sama seperti menu walking, menu cycling ini akan melakukan pelacakan aktivitas olahraga pengguna apabila pengguna ingin bersepeda. Aplikasi akan melacak lokasi pengguna menggunakan GPS, dan menampilkan jarak yang telah ditempuh oleh pengguna ketika sedang bersepeda. Seperti menu walking, menu cycling juga dapat berjalan di background dan akan menampilkan notifikasi di smartphone pengguna. 

Ketika pengguna selesai, pengguna akan ditampilkan tampilan informasi latihan yang telah dilakukan, serta akan ditampilkan maps untuk rute dari training yang telah dilakukan. Dan juga informasi training tersebut akan disimpan pada history.

### History
Pada menu ini, pengguna dapat melihat list aktivitas training yang telah dilakukan pengguna pada hari-hari sebelumnya. Pengguna akan ditampilkan kalender untuk dapat memilih tanggal berapa yang ingin dilihat aktivitas trainingnya, kemudian setelah memilih tanggal pada kalender tersebut, pengguna dapat melihat setiap training yang telah dilakukan pada tanggal tersebut. Kemudian pengguna dapat melihat detail dari setiap training tersebut dengan klik item pada list tersebut. Kemudian pengguna akan ditampilkan detail dari walking ataupun cycling.

### Scheduler
Pengguna dapat mengatur jadwal untuk latihan yang akan pengguna lakukan, dan memilih untuk dapat melakukan training :
1. Spesifik satu waktu
2. Rutin per hari.
3. Rutin per hari tertentu.

Selain itu, pengguna dapat mengatur juga tipe training apakah cycling atau walking. Serta menetapkan target sesuai masukkan dari pengguna (5 km untuk cycling atau 10000 step untuk walking). Pengguna juga dapat mengatur agar pelacakan dapat berjalan sesuai otomatis saat waktu mulai jadwal latihan dengan turn on "Auto Tracking".

Saat waktu sudah sesuai jadwal, pengguna akan ditampilkan notifikasi sesuai tipe dan target latihan. Dan apabila pengguna memilih menyalakan fitur auto tracking, maka pelacakan sesuai tipe training akan dijalankan, dan ketika waktu selesai sesuai jadwal atau pengguna mematikan pelacakannya, training akan disimpan pada history.

## Library yang digunakan
1. Retrofit : Digunakan untuk pemanggilan API.
2. RxJava : Digunakan untuk handling response dari pemanggilan API yang telah dilakukan menggunakan retrofit.
3. Glide : Digunakan untuk menampilkan gambar dari sport news.
4. Room : Digunakan untuk menyimpan training history dan schedule.

## Screenshot Aplikasi

## Pembagian Kerja Anggota Kelompok
1. Yahya - 13518029 : Sport News, Tracking Walking & Cycling, Menampilkan maps pada detail cycling
2. Jonet Wira Murti - 13518083 : Training History, Detail Walking & Cycling, Menampilkan maps pada detail cycling
3. Felicia Gojali - 13518101 : Training Scheduler, Menampilkan notifikasi saat mulai jadwal training & auto tracking saat mulai jadwal training.
