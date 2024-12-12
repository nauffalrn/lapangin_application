// Simulasi booking lapangan
const bookingData = [
  {
    lapangan: "MB Futsal",
    waktuMulai: "2024-11-23T18:00:00",
  },
];

// Fungsi untuk menghitung waktu pengingat
function setReminder(booking) {
  const now = new Date(); // Waktu sekarang
  const waktuMulai = new Date(booking.waktuMulai); // Waktu booking dimulai
  const pengingatWaktu = new Date(waktuMulai.getTime() - 60 * 60 * 1000); // 1 jam sebelum

  // Jika pengingat waktu belum terlewat
  if (pengingatWaktu > now) {
    const timeToReminder = pengingatWaktu - now; // Selisih waktu dalam milidetik
    setTimeout(() => {
      tampilkanNotifikasi(booking);
    }, timeToReminder);
  }
}

// Fungsi untuk menampilkan notifikasi
function tampilkanNotifikasi(booking) {
  const notificationList = document.getElementById("notification-list");
  const notificationItem = document.createElement("div");
  notificationItem.className = "notification-item";
  notificationItem.innerHTML = `
        <div class="icon">⏰</div>
        <div class="notification-content">
            <h4>Pengingat: Lapangan ${booking.lapangan}</h4>
            <p>Jadwal Anda dimulai dalam 1 jam (${new Date(
              booking.waktuMulai
            ).toLocaleTimeString()}). Siapkan perlengkapan Anda!</p>
        </div>
    `;
  notificationList.appendChild(notificationItem);
}

// Inisialisasi pengingat untuk semua booking
bookingData.forEach(setReminder);
