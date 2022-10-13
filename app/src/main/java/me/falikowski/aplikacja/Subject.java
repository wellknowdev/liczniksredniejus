package me.falikowski.aplikacja;

public class Subject {

    private String kod;
    private String nazwa;
    private String ocena_main;
    private String ects;
    private String semestr;
    private String rok;


    Subject(){}

    Subject(String t_kod, String t_nazwa, String t_ocena, String t_ects, String t_semestr) {
        String[] t_rok = t_semestr.split(" ");
        this.kod = t_kod;
        this.nazwa = t_nazwa;
        this.ocena_main = t_ocena;
        this.ects = t_ects;
        this.semestr = t_semestr;
        this.rok = t_rok[2];
    }


    public String getNazwa() {
        return nazwa;
    }

    public void setName(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getOcena() {
        return ocena_main;
    }

    public String getECTS() {
        return ects;
    }

    public String getSemester() {
        return semestr;
    }
    public String getRok() {
        return rok;
    }

    public String getKod() {
        return kod;
    }

    @Override
    public String toString() {
        return "Subject [kod_p=" + this.kod + ", nazwa=" + this.nazwa + ", ocena=" + this.ocena_main
                + ", ects=" + this.ects + ", semestr=" + this.semestr + "]";
    }
}
