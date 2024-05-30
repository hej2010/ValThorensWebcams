package se.swecookie.valthorens.data;

public enum Webcam {
    CHOOSE_FROM_MAP(0, true, null, null),
    LIVECAM_360(1, false, "Livecam 360\u00B0", "http://www.skaping.com/valthorens/station"),
    DE_LA_MAISON(2, false, "De La Maison", "http://www.skaping.com/valthorens/lamaison"),
    LES_2_LACS(3, false, "Les 2 Lacs", "http://www.skaping.com/valthorens/2lacs"),
    FUNITEL_DE_THORENS(4, false, "Funitel De Thorens", "http://www.skaping.com/valthorens/funitelthorens"),
    FUNITEL_3_VALLEES(5, false, "Funitel 3 Vallées", "http://www.skaping.com/valthorens/3vallees"),
    STADE(6, false, "Stade", "http://www.skaping.com/valthorens/stade"),
    BOISMINT(7, false, "Boismint", "http://www.skaping.com/valthorens/boismint"),
    LA_TYROLIENNE(8, true, "La Tyrolienne", "http://www.valthorens.com/en/webcam/livecam-tyrolienne", "http://www.trinum.com/ibox/ftpcam/small_val_thorens_tyrolienne.jpg", "http://www.trinum.com/ibox/ftpcam/original_val_thorens_tyrolienne.jpg"),
    PLAN_BOUCHET(9, true, "Plan Bouchet", "http://www.valthorens.com/en/webcam/webcam-plan-bouchet", "http://www.trinum.com/ibox/ftpcam/small_orelle_sommet-tc-orelle.jpg", "http://www.trinum.com/ibox/ftpcam/original_orelle_sommet-tc-orelle.jpg"),
    PLEIN_SUD(10, true, "Plein Sud", "http://www.valthorens.com/en/webcam/livecam-la-folie-douce-plein-sud", "http://www.trinum.com/ibox/ftpcam/small_val_thorens_funitel-bouquetin.jpg"),
    CIME_CARON(11, true, "Cime Caron", "http://www.valthorens.com/en/webcam/livecam-cime-caron", "http://www.trinum.com/ibox/ftpcam/small_val_thorens_cime-caron.jpg", "http://www.trinum.com/ibox/ftpcam/original_val_thorens_cime-caron.jpg");

    public final int i;
    public final String name, url, previewUrl, staticImageUrl;
    public final boolean isStatic;

    Webcam(int i, boolean isStatic, String name, String url) {
        this(i, isStatic, name, url, null, null);
    }

    Webcam(int i, boolean isStatic, String name, String url, String previewUrl) {
        this(i, isStatic, name, url, previewUrl, null);
    }

    Webcam(int i, boolean isStatic, String name, String url, String previewUrl, String staticImageUrl) {
        this.i = i;
        this.isStatic = isStatic;
        this.name = name;
        this.url = url;
        this.previewUrl = previewUrl;
        this.staticImageUrl = staticImageUrl;
    }

    public static final int NR_OF_WEBCAMS = 11;

    public static Webcam fromInt(int i) {
        switch (i) {
            case 1:
                return LIVECAM_360;
            case 2:
                return DE_LA_MAISON;
            case 3:
                return LES_2_LACS;
            case 4:
                return FUNITEL_DE_THORENS;
            case 5:
                return FUNITEL_3_VALLEES;
            case 6:
                return STADE;
            case 7:
                return BOISMINT;
            case 8:
                return LA_TYROLIENNE;
            case 9:
                return PLAN_BOUCHET;
            case 10:
                return PLEIN_SUD;
            case 11:
                return CIME_CARON;
            case 0:
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
