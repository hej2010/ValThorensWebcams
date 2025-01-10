package se.swecookie.valthorens.data;

public enum Webcam {
    CHOOSE_FROM_MAP(0, true, null, null, null),
    LIVECAM_360(1, false, "Livecam 360°", "http://www.skaping.com/valthorens/station", null),
    DE_LA_MAISON(2, false, "De La Maison", "http://www.skaping.com/valthorens/lamaison", null),
    LES_2_LACS(3, false, "Les 2 Lacs", "http://www.skaping.com/valthorens/2lacs", null),
    FUNITEL_DE_THORENS(4, false, "Funitel De Thorens", "http://www.skaping.com/valthorens/funitelthorens", null),
    FUNITEL_3_VALLEES(5, false, "Funitel 3 Vallées", "http://www.skaping.com/valthorens/3vallees", null),
    STADE(6, false, "Stade", "http://www.skaping.com/valthorens/stade", null),
    BOISMINT(7, false, "Boismint", "http://www.skaping.com/valthorens/boismint", null),
    LA_TYROLIENNE(8, true, "La Tyrolienne", "https://www.valthorens.com/en/webcam/webcam-pointe-du-bouchet-tyrolienne/", "http://www.trinum.com/ibox/ftpcam/small_val_thorens_tyrolienne.jpg"),
    PLAN_BOUCHET(9, true, "Plan Bouchet", "https://www.valthorens.com/en/webcam/webcam-plan-bouchet/", "http://www.trinum.com/ibox/ftpcam/small_orelle_sommet-tc-orelle.jpg"),
    //PROSNEIGE_SKI_SCHOOL(10, false, "Prosneige Ski School", "https://www.valthorens.com/en/webcam/webcam-ecole-de-ski-prosneige/", null),
    PLEIN_SUD(10, true, "Plein Sud", "http://www.valthorens.com/en/webcam/livecam-la-folie-douce-plein-sud", "http://www.trinum.com/ibox/ftpcam/small_val_thorens_funitel-bouquetin.jpg"),
    CIME_CARON(11, true, "Cime Caron", "https://www.valthorens.com/en/webcam/webcam-cime-caron/", "http://www.trinum.com/ibox/ftpcam/small_val_thorens_cime-caron.jpg");

    public final int i;
    public final String name, url, previewUrl;
    public final boolean isStatic;

    Webcam(int i, boolean isStatic, String name, String url, String previewUrl) {
        this.i = i;
        this.isStatic = isStatic;
        this.name = name;
        this.url = url;
        this.previewUrl = previewUrl;
    }

    public static final int NR_OF_WEBCAMS = 11;

    public static Webcam fromInt(int i) {
        return switch (i) {
            case 1 -> LIVECAM_360;
            case 2 -> DE_LA_MAISON;
            case 3 -> LES_2_LACS;
            case 4 -> FUNITEL_DE_THORENS;
            case 5 -> FUNITEL_3_VALLEES;
            case 6 -> STADE;
            case 7 -> BOISMINT;
            case 8 -> LA_TYROLIENNE;
            case 9 -> PLAN_BOUCHET;
            case 10 -> PLEIN_SUD;
            case 11 -> CIME_CARON;
            default -> CHOOSE_FROM_MAP;
        };
    }
/*
0 choose from map
1 funitel 3 vallees
2 de la maison
3 les 2 lacs
4 funitel de thorens
5 stade
6 boismint
7 la tyrolienne
8 plan bouchet
9 livecam 360
10 plein sud
11 cime caron
*/

}
