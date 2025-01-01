document.addEventListener("DOMContentLoaded", () => {
    const searchInput = document.querySelector('input[placeholder="Nama Lapangan"]');
    const kotaSelect = document.querySelector('select:nth-of-type(1)');
    const olahragaSelect = document.querySelector('select:nth-of-type(2)');
    const cariButton = document.querySelector('.filter-container button:last-of-type'); // Tombol "Cari venue"
    const venues = document.querySelectorAll('.pro');
    const noResults = document.getElementById("no-results");

    function filterVenues() {
        const searchText = searchInput.value.toLowerCase();
        const selectedKota = kotaSelect.value.toLowerCase();
        const selectedOlahraga = olahragaSelect.value.toLowerCase();
        let hasVisible = false;

        venues.forEach(venue => {
            const nama = venue.dataset.nama.toLowerCase();
            const kota = venue.dataset.kota.toLowerCase();
            const olahraga = venue.dataset.olahraga.toLowerCase();

            if (
                (nama.includes(searchText) || searchText === "") &&
                (kota === selectedKota || selectedKota === "pilih daerah") &&
                (olahraga === selectedOlahraga || selectedOlahraga === "pilih cabang olahraga")
            ) {
                venue.style.display = "block"; // Tampilkan elemen
                hasVisible = true;
            } else {
                venue.style.display = "none"; // Sembunyikan elemen
            }
        });

        if (hasVisible) {
            noResults.style.display = "none";
        } else {
            noResults.style.display = "block";
        }
    }

    // Event listener hanya untuk tombol "Cari venue"
    cariButton.addEventListener("click", filterVenues);
});