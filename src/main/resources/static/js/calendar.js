document.addEventListener("DOMContentLoaded", function () {
  const calendarDays = document.getElementById("calendar-days");
  const monthYear = document.getElementById("month-year");
  const prevMonthBtn = document.getElementById("prev-month");
  const nextMonthBtn = document.getElementById("next-month");

  let currentDate = new Date();

  function renderCalendar(date) {
    calendarDays.innerHTML = "";
    const year = date.getFullYear();
    const month = date.getMonth();

    const firstDay = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();

    monthYear.textContent = `${date.toLocaleString("default", {
      month: "long",
    })} ${year}`;

    for (let i = 0; i < firstDay; i++) {
      const emptyCell = document.createElement("div");
      calendarDays.appendChild(emptyCell);
    }

    for (let day = 1; day <= daysInMonth; day++) {
      const dayCell = document.createElement("div");
      dayCell.textContent = day;

      const today = new Date();
      if (
        day === today.getDate() &&
        month === today.getMonth() &&
        year === today.getFullYear()
      ) {
        dayCell.classList.add("today");
      }

      dayCell.addEventListener("click", () => {
        document
          .querySelectorAll(".calendar-days div")
          .forEach((div) => div.classList.remove("selected"));
        dayCell.classList.add("selected");
      });

      calendarDays.appendChild(dayCell);
    }
  }

  prevMonthBtn.addEventListener("click", () => {
    currentDate.setMonth(currentDate.getMonth() - 1);
    renderCalendar(currentDate);
  });

  nextMonthBtn.addEventListener("click", () => {
    currentDate.setMonth(currentDate.getMonth() + 1);
    renderCalendar(currentDate);
  });

  renderCalendar(currentDate);
});
