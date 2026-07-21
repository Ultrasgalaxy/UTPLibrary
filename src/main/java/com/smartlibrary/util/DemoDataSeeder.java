package com.smartlibrary.util;

import com.smartlibrary.facade.LibraryFacade;

/** Los datos se gestionan desde la base de datos MySQL. */
public final class DemoDataSeeder {
    private DemoDataSeeder() {}
    public static void seed(LibraryFacade facade) {
        // Sin datos demo: todo viene de utp_library en MySQL
    }
}
