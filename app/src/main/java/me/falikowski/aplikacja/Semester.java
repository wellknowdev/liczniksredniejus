package me.falikowski.aplikacja;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class Semester{

    private Subject subject = new Subject();
    private List<Subject> przedmioty = new ArrayList<Subject>();


    public Map<String, Float> loadSemesters(Map<String, String> loggedCookies) throws IOException {

        Connection.Response resOceny = Jsoup.connect("https://usosweb.us.edu.pl/kontroler.php?_action=dla_stud/studia/oceny/index")
                .userAgent(MainActivity.userAgent)
                .cookies(loggedCookies)
                .execute();

        Document ocenyDocument = resOceny.parse();
        Elements ocenki = ocenyDocument.getElementsByTag("usos-frame");
        Elements divs = ocenki.select("div.expand-collapse");

        Connection.Response resEcts = Jsoup.connect("https://usosweb.us.edu.pl/kontroler.php?_action=dla_stud/studia/polon")
                .userAgent(MainActivity.userAgent)
                .cookies(loggedCookies)
                .execute();

        Document ectsDocument = resEcts.parse();
        Elements ectski = ectsDocument.select("table[class=grey]");


        for (Element e : divs) {
            if(String.valueOf(e).contains("javascript:void(0);")) {
                String x[] = e.text().split("Przedmiot");
                if(x[0].startsWith("semestr")) {
                    Elements tt = e.select("tbody.autostrong");
                    Elements ty = tt.select("tr");

                    for (Element r : ty) {
                        String ocena = null;
                        String przedmiot = r.select("td[style=\"width: 50%;\"]").first().text();
                        String kod_przedmiotu = przedmiot.substring(przedmiot.indexOf("[") + 1, przedmiot.indexOf("]"));
                        String ects = null;
                        przedmiot = przedmiot.replace(kod_przedmiotu, "");
                        try {
                            for(Element d : ectski) {
                                Elements da = d.select("tr");
                                for(Element z : da) {
                                    if (String.valueOf(z).contains(kod_przedmiotu)) {
                                        ects = String.valueOf(Double.parseDouble(z.text().substring(z.text().lastIndexOf(' ') + 1).trim()));
                                    }
                                }
                            }
                            ocena = r.select("td[style=\"text-align:right; white-space:nowrap; width: 15%;\"] span[style=font-weight:bold; font-size: 115%;]").first().text();
                        }
                        catch (NullPointerException err) {
                            ocena = null;
                            ects = null;
                        }
                        catch (NumberFormatException err) {
                            ects = null;
                        }
                        Subject s = new Subject(kod_przedmiotu, przedmiot, ocena, ects, x[0]);
                        przedmioty.add(s);
                    }
                }
            }
        }
        return divideBySemesters();
    }


    public Map<String, Float> divideBySemesters()  {
        Map<String, Float> mapOfLists = new HashMap<String, Float>();
        List<String> semesters = new ArrayList<>();
        List<Float> means = new ArrayList<>();
        przedmioty.forEach(subject -> {
            if(! semesters.contains("rok akademicki "+subject.getRok())) {
                float mean = 0;
                int weights = 0;
                semesters.add("rok akademicki "+subject.getRok());
                for (Subject subject_mean : przedmioty) {
                    try{
                    if(Objects.equals(subject_mean.getRok(), subject.getRok())) {
                        mean += Float.parseFloat(String.valueOf(subject_mean.getOcena()).replace(",", ".")) * Float.parseFloat(String.valueOf(subject_mean.getECTS()));
                        weights+=Float.parseFloat(String.valueOf(subject_mean.getECTS()));
                    }
                } catch (NumberFormatException err) {
                        err.printStackTrace();
                    }
                }
                mean = mean / weights;
                mapOfLists.put("rok akademicki "+subject.getRok(), mean);
            }
            if(! semesters.contains(subject.getSemester())) {
                float mean = 0;
                int weights = 0;
                semesters.add(subject.getSemester());
                for (Subject subject_mean : przedmioty) {
                    try{
                        if(Objects.equals(subject_mean.getSemester(), subject.getSemester())) {
                            mean += Float.parseFloat(String.valueOf(subject_mean.getOcena()).replace(",", ".")) * Float.parseFloat(String.valueOf(subject_mean.getECTS()));
                            weights+=Float.parseFloat(String.valueOf(subject_mean.getECTS()));
                        }
                    } catch (NumberFormatException err) {
                        err.printStackTrace();//pomijam null'e oraz wartosci ktore nie sa liczbami
                    }
                }
                mean = mean / weights;
                mapOfLists.put(subject.getSemester(), mean);
            }
        });
        return new TreeMap<>(mapOfLists);
    }

}