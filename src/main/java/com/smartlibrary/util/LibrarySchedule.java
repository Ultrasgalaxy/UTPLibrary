package com.smartlibrary.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Reglas de horario — Biblioteca Filial Piura UTP
 * Lun-Sab: 8:00 am – 10:00 pm  |  Dom: 9:00 am – 1:00 pm
 * Ventana de reserva: hoy + los proximos 6 dias (7 dias en total).
 */
public final class LibrarySchedule {

    private LibrarySchedule() {}

    private static final LocalTime WEEKDAY_OPEN  = LocalTime.of(8,  0);
    private static final LocalTime WEEKDAY_CLOSE = LocalTime.of(22, 0);
    private static final LocalTime SUNDAY_OPEN   = LocalTime.of(9,  0);
    private static final LocalTime SUNDAY_CLOSE  = LocalTime.of(13, 0);

    /**
     * Los proximos 7 dias validos a partir de hoy. Si el dia de hoy ya no
     * tiene ninguna franja horaria disponible (por la hora actual), se omite
     * y la ventana de 7 dias arranca directamente desde manana, para que
     * siempre haya correlacion entre la fecha mostrada y sus horas.
     */
    public static List<LocalDate> getAvailableDates() {
        LocalDate today = LocalDate.now();
        int start = getSlots(today).isEmpty() ? 1 : 0;

        List<LocalDate> dates = new ArrayList<>();
        for (int i = start; i < start + 7; i++) {
            dates.add(today.plusDays(i));
        }
        return dates;
    }

    /**
     * Franjas horarias cada 30 min para el dia dado. Si el dia es HOY, se
     * excluyen las horas ya transcurridas: solo se muestran las franjas que
     * todavia estan por venir segun la hora actual.
     */
    public static List<String> getSlots(LocalDate date) {
        boolean isSunday = date.getDayOfWeek() == DayOfWeek.SUNDAY;
        LocalTime open  = isSunday ? SUNDAY_OPEN  : WEEKDAY_OPEN;
        LocalTime close = isSunday ? SUNDAY_CLOSE : WEEKDAY_CLOSE;
        LocalTime last  = close.minusMinutes(30);

        LocalTime start = open;
        if (date.equals(LocalDate.now())) {
            LocalTime nextSlot = nextSlotAfter(LocalTime.now());
            if (nextSlot.isAfter(start)) start = nextSlot;
        }

        List<String> slots = new ArrayList<>();
        LocalTime t = start;
        while (!t.isAfter(last)) {
            slots.add(formatSlot(t));
            t = t.plusMinutes(30);
        }
        return slots;
    }

    /** Redondea hacia arriba a la siguiente franja de 30 min, estrictamente futura. */
    private static LocalTime nextSlotAfter(LocalTime now) {
        int totalMinutes = now.getHour() * 60 + now.getMinute();
        int remainder    = totalMinutes % 30;
        int nextTotal    = (remainder == 0) ? totalMinutes + 30 : totalMinutes + (30 - remainder);
        if (nextTotal >= 24 * 60) return LocalTime.of(23, 59); // ya no queda nada hoy
        return LocalTime.of(nextTotal / 60, nextTotal % 60);
    }

    /** Etiqueta legible para mostrar en el ComboBox de fechas. */
    public static String formatDate(LocalDate d) {
        String dayName = switch (d.getDayOfWeek()) {
            case MONDAY    -> "Lunes";
            case TUESDAY   -> "Martes";
            case WEDNESDAY -> "Miercoles";
            case THURSDAY  -> "Jueves";
            case FRIDAY    -> "Viernes";
            case SATURDAY  -> "Sabado";
            case SUNDAY    -> "Domingo";
        };
        // Marca el dia de hoy para que sea evidente
        String today = d.equals(LocalDate.now()) ? " (hoy)" : "";
        return dayName + today + " " +
               String.format("%02d", d.getDayOfMonth()) + "/" +
               String.format("%02d", d.getMonthValue());
    }

    private static String formatSlot(LocalTime t) {
        int h    = t.getHour();
        String ampm = h < 12 ? "am" : "pm";
        int h12  = (h == 0) ? 12 : (h > 12) ? h - 12 : h;
        return String.format("%d:%02d %s", h12, t.getMinute(), ampm);
    }
}
