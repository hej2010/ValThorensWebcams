package se.swecookie.valthorens;

enum Webcam {
    CHOOSE_FROM_MAP(0),
    FUNITEL_3_VALLEES(1),
    DE_LA_MAISON(2),
    LES_2_LACS(3),
    FUNITEL_DE_THORENS(4),
    STADE(5),
    BOISMINT(6),
    LA_TYROLIENNE(7),
    PLAN_BOUCHET(8),
    LIVECAM_360(9),
    PLEIN_SUD(10),
    CIME_CARON(11);

    final int i;

    Webcam(int i) {
        this.i = i;
    }

    public static final int NR_OF_WEBCAMS = 11;
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
