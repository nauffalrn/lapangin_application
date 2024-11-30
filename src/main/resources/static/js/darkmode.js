const toggleSwitch = document.getElementById('dark-mode-toggle');

// Cek apakah mode gelap disimpan di localStorage
const currentMode = localStorage.getItem('dark-mode');
if (currentMode === 'enabled') {
    document.body.classList.add('dark-mode'); // Aktifkan mode gelap
    toggleSwitch.checked = true; // Setel toggle ke posisi "checked"
} else {
    document.body.classList.remove('dark-mode'); // Pastikan mode terang
    toggleSwitch.checked = false; // Setel toggle ke posisi "unchecked"
}

// Tambahkan event listener untuk toggle
toggleSwitch.addEventListener('change', function () {
    if (toggleSwitch.checked) {
        document.body.classList.add('dark-mode'); // Aktifkan mode gelap
        localStorage.setItem('dark-mode', 'enabled'); // Simpan status
    } else {
        document.body.classList.remove('dark-mode'); // Kembali ke mode terang
        localStorage.setItem('dark-mode', 'disabled'); // Simpan status
    }
});