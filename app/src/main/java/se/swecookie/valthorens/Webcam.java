package se.swecookie.valthorens;

enum Webcam {
    CHOOSE_FROM_MAP(0, null, null, null),
    FUNITEL_3_VALLEES(1, "Funitel 3 Vallées", "http://skaping.com/valthorens/3vallees", null),
    DE_LA_MAISON(2, "De La Maison", "http://skaping.com/valthorens/lamaison", null),
    LES_2_LACS(3, "Les 2 Lacs", "http://skaping.com/valthorens/2lacs", null),
    FUNITEL_DE_THORENS(4, "Funitel De Thorens", "http://skaping.com/valthorens/funitelthorens", null),
    STADE(5, "Stade", "http://www.skaping.com/valthorens/stade", null),
    BOISMINT(6, "Boismint", "http://www.skaping.com/valthorens/boismint", null),
    LA_TYROLIENNE(7, "La Tyrolienne", "http://www.valthorens.com/en/webcam/livecam-tyrolienne", "http://www.trinum.com/ibox/ftpcam/small_val_thorens_tyrolienne.jpg"),
    PLAN_BOUCHET(8, "Plan Bouchet", "http://www.valthorens.com/en/webcam/livecam-plan-bouchet", "http://www.trinum.com/ibox/ftpcam/small_orelle_sommet-tc-orelle.jpg"),
    LIVECAM_360(9, "Livecam 360\u00B0", "http://www.valthorens.com/en/webcam/livecam-station", null),
    PLEIN_SUD(10, "Plein Sud", "http://www.valthorens.com/en/webcam/livecam-la-folie-douce-plein-sud", "http://www.trinum.com/ibox/ftpcam/small_val_thorens_funitel-bouquetin.jpg"),
    CIME_CARON(11, "Cime Caron", "http://www.valthorens.com/en/webcam/livecam-cime-caron", "http://www.trinum.com/ibox/ftpcam/small_val_thorens_cime-caron.jpg");

    final int i;
    final String name, url, previewUrl;

    Webcam(int i, String name, String url, String previewUrl) {
        this.i = i;
        this.name = name;
        this.url = url;
        this.previewUrl = previewUrl;
    }

    public static final int NR_OF_WEBCAMS = 11;

    public static Webcam fromInt(int i) {
        switch (i) {
            case 0:
                return CHOOSE_FROM_MAP;
            case 1:
                return FUNITEL_3_VALLEES;
            case 2:
                return DE_LA_MAISON;
            case 3:
                return LES_2_LACS;
            case 4:
                return FUNITEL_DE_THORENS;
            case 5:
                return STADE;
            case 6:
                return BOISMINT;
            case 7:
                return LA_TYROLIENNE;
            case 8:
                return PLAN_BOUCHET;
            case 9:
                return LIVECAM_360;
            case 10:
                return PLEIN_SUD;
            case 11:
                return CIME_CARON;
            default:
                return CHOOSE_FROM_MAP;
        }
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
