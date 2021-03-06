package uk.recurse.geocoding.reverse;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.io.StringReader;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ReverseGeocoderTest {

    private static ReverseGeocoder geocoder;

    private static final String INPUT = "GB\tGBR\t826\tUK\tUnited Kingdom\tLondon\t244820\t62348447\tEU\t.uk\tGBP\tPound\t44\t@# #@@|@## #@@|@@# #@@|@@## #@@|@#@ #@@|@@#@ #@@|GIR0AA\t^((?:(?:[A-PR-UWYZ][A-HK-Y]\\d[ABEHMNPRV-Y0-9]|[A-PR-UWYZ]\\d[A-HJKPS-UW0-9])\\s\\d[ABD-HJLNP-UW-Z]{2})|GIR\\s?0AA)$\ten-GB,cy-GB,gd\t2635167\tIE";

    private static Country uk;




    @BeforeAll
    static void setup() {
        geocoder = new ReverseGeocoder();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/baselineCities.csv")
    void reverseGeocoding(float lat, float lon, String expectedIso) {
        String actualIso = geocoder.getCountry(lat, lon)
                .map(Country::iso)
                .orElseGet(() -> fail("Country not found"));

        assertEquals(expectedIso, actualIso, "lat=" + lat + " lon=" + lon);
    }

    @Test
    void streaming() {
        assertEquals(247, geocoder.countries().count());
    }


    @Test
    // Test1: Check whether lat and lon can be found in geo data
    void TestErrorthrowInt()
    {
        String result = "TH"+"Thailand";
        assertThrows(NoSuchElementException.class, ()->{
            assertEquals(result, geocoder.getCountry(123.40008,213.34658).map(Country::iso).get()+geocoder.getCountry(123.40008,213.34658).map(Country::name).get());
            assertEquals(result, geocoder.getCountry(-95.36359,-188.36648).map(Country::iso).get()+geocoder.getCountry(-95.36359,-188.36648).map(Country::name).get());
        });

        assertDoesNotThrow( ()->{
            assertEquals(result, geocoder.getCountry(7.88481,98.40008).map(Country::iso).get()+geocoder.getCountry(7.88481,98.40008).map(Country::name).get());
        });

    }
    @Test
    void TestErrorthrowFunc()
    {
        String result = "TH"+"Thailand";
        assertThrows(NoSuchElementException.class, ()->{
            assertEquals(result, geocoder.getCountry(13.659078,96.194694).map(Country::iso).get()+geocoder.getCountry(13.659078,96.194694).map(Country::name).get());
        });

        assertDoesNotThrow( ()->{
            assertEquals(result, geocoder.getCountry(7.88481,98.40008).map(Country::iso).get()+geocoder.getCountry(7.88481,98.40008).map(Country::name).get());
        });

    }

    @Test
    // Test2: Check if this lat & lon match the country name in geo data
    void TestfindcountrynameInt()
    {
        assertThrows(NoSuchElementException.class , ()-> {
            assertEquals("Thailand", geocoder.getCountry(98.40008,250.34277).map(Country::name).get());
        });
        assertDoesNotThrow( () -> {
            assertEquals("Thailand", geocoder.getCountry(7.88481,98.40008).map(Country::name).get());

        });
    }

    @Test
    void TestfindcountrynameFunc()
    {
        assertThrows(NoSuchElementException.class , ()-> {
            assertEquals("Finland", geocoder.getCountry(-161.55632,523.59606).map(Country::name).get());
        });
        assertDoesNotThrow( () -> {
            assertEquals("Fiji", geocoder.getCountry(-18.14161,178.44149).map(Country::name).get());
            assertEquals("Falkland Islands", geocoder.getCountry(-51.69382,-57.85701).map(Country::name).get());
            assertEquals("Faroe Islands", geocoder.getCountry(62.00973,-6.77164).map(Country::name).get());
            assertEquals("France", geocoder.getCountry(48.71785,2.49338).map(Country::name).get());
            assertEquals("French Polynesia", geocoder.getCountry(-17.63333,-149.6).map(Country::name).get());
            assertEquals("French Southern Territories", geocoder.getCountry(-49.34916,70.21937).map(Country::name).get());
        });
    }

    @Test
    //Test3: Check boundary of two adjacent country in geo data
    void TestadjacentcountryboundaryInt()
    {

        assertThrows(NoSuchElementException.class, ()->{
            geocoder.getCountry(100, -180).get();
            geocoder.getCountry(17.711150, -180).get();
            geocoder.getCountry(100, 116.873733).get();
        });

        assertDoesNotThrow(()->{
            assertEquals("Thailand", geocoder.getCountry(17.711150, 104.411472).map(Country::name).get());
        });
    }
    @Test
    void TestadjacentcountryboundaryFunc(){
        assertDoesNotThrow(()->{
            assertEquals("Laos", geocoder.getCountry(17.96667, 102.6).map(Country::name).get());
            assertEquals("Myanmar", geocoder.getCountry(20.46504, 94.8712).map(Country::name).get());
            assertEquals("Cambodia", geocoder.getCountry(11.56245, 104.91601).map(Country::name).get());
            assertEquals("Malaysia", geocoder.getCountry(6.32649, 99.8432).map(Country::name).get());
        });
    }

    @Test
    //Test4: Check boundary of continent in geo data
    void TestcountryboundaryInc()
    {
        assertThrows(NoSuchElementException.class, ()->{
            assertEquals("Australia", geocoder.getCountry(100, 116.932848).map(Country::name).get());
            assertEquals("Australia", geocoder.getCountry(-34.704150, -100).map(Country::name).get());
            assertEquals("Australia", geocoder.getCountry(100, -100).map(Country::name).get());
        });

        assertDoesNotThrow(()->{
            assertEquals("Australia", geocoder.getCountry(-34.704150, 116.932848).map(Country::name).get());
        });

    }
    @Test
    void TestcountryboundaryFunc()
    {
        assertThrows(NoSuchElementException.class, ()->{
            assertEquals("Australia", geocoder.getCountry(-35.015031, 116.765748).map(Country::name).get());
            assertEquals("Australia", geocoder.getCountry(-68.065051, 124.403856).map(Country::name).get());
            assertEquals("Australia", geocoder.getCountry(-63.050280, 122.564863).map(Country::name).get());
        });

        assertDoesNotThrow(()->{
            assertEquals("Australia", geocoder.getCountry(-34.704150, 116.932848).map(Country::name).get());
        });
    }

    @Test
    //Test5: Check if this lat & lon match the country iso in geo data
    void TestcountryisoInt()
    {
        assertThrows(NoSuchElementException.class , ()-> {
            assertEquals("TH", geocoder.getCountry(-95.36359,104.411472).map(Country::iso).get());
            assertEquals("TH", geocoder.getCountry(17.711150,213.34658).map(Country::iso).get());
        });
        assertDoesNotThrow( () -> {
            assertEquals("TH", geocoder.getCountry(7.88481,98.40008).map(Country::iso).get());

        });

    }
    @Test
    void TestcountryisoFunc()
    {
        assertThrows(NoSuchElementException.class , ()-> {
            assertEquals("RE", geocoder.getCountry(0,98.40008).map(Country::iso).get());
        });
        assertDoesNotThrow( () -> {
            assertEquals("RE", geocoder.getCountry(-21.3393,55.47811).map(Country::iso).get());
            assertEquals("RO", geocoder.getCountry(43.65638,25.36454).map(Country::iso).get());
            assertEquals("RS", geocoder.getCountry(45.38361,20.38194).map(Country::iso).get());
            assertEquals("RU", geocoder.getCountry(57.87944,34.9925).map(Country::iso).get());
            assertEquals("RW", geocoder.getCountry(-1.9487,30.4347).map(Country::iso).get());
        });
    }

    @Test
    //Test6: Check minimum lat and long to input
    void TestminmaxlatlonInt()
    {
        assertThrows(NoSuchElementException.class, ()->{
            geocoder.getCountry(-7.71833,0).get();
            geocoder.getCountry(36.6825,0).get();
            geocoder.getCountry(0,-102.32554).get();
            geocoder.getCountry(0,124.73333).get();
            geocoder.getCountry(0,0).get();
        });
        assertDoesNotThrow( ()->{
            assertEquals("Brazil",geocoder.getCountry(-23.64889,-46.85222).map(Country::name).get());
            assertEquals("Mexico",geocoder.getCountry(20.62445,-103.23423).map(Country::name).get());
            assertEquals("China",geocoder.getCountry(52.33333,124.73333).map(Country::name).get());
        });
    }
    @Test
    void TestminmaxlatlonFunc()
    {
        assertThrows(NoSuchElementException.class, ()->{
            geocoder.getCountry(120.4583,320.4353).get();
            geocoder.getCountry(28.9349,-200.0043).get();
            geocoder.getCountry(140.466,28.9349).get();
        });

        assertDoesNotThrow( ()->{
            assertEquals("Zimbabwe",geocoder.getCountry(-18.20476,28.9349).map(Country::name).get());
        });
    }


    @Test
    //Test7: Check local languages for each country in geo data (locales ordered by the number of speakers)
    void TestlocalesInt()
    {
        List<Locale> thlocales = Arrays.asList(
                new Locale("th"),
                new Locale("en")
        );
        assertThrows(NoSuchElementException.class , ()-> {
            assertEquals(thlocales, geocoder.getCountry(-95.72343,250.34277).map(Country::continent).get());
            assertEquals(thlocales, geocoder.getCountry(-95.72343,98.40008).map(Country::continent).get());
            assertEquals(thlocales, geocoder.getCountry(7.88481,250.34277).map(Country::continent).get());
        });
        assertDoesNotThrow( () -> {
            assertEquals(thlocales, geocoder.getCountry(7.88481,98.40008).map(Country::locales).get());
        });

    }
    @Test
    void TestlocalesFunc()
    {
        List<Locale> thlocales = Arrays.asList(
                new Locale("th"),
                new Locale("en")
        );

        List<Locale> uslocales = Arrays.asList(
                new Locale("en", "US"),
                new Locale("es", "US"),
                new Locale("haw"),
                new Locale("fr")
        );

        assertThrows(NoSuchElementException.class , ()-> {
            assertEquals(thlocales, geocoder.getCountry(0,0).map(Country::continent).get());
            assertEquals(uslocales, geocoder.getCountry(0,0).map(Country::continent).get());
        });
        assertDoesNotThrow( () -> {
            assertEquals(thlocales, geocoder.getCountry(7.88481,98.40008).map(Country::locales).get());
            assertEquals(uslocales, geocoder.getCountry(38.73289,-77.05803).map(Country::locales).get());
        });
    }

    @Test
    //Test8: Check if this lat & lon match the country continent in geo data
    void TestfindcontinentInt()
    {
        assertThrows(NoSuchElementException.class , ()-> {
            assertEquals("AS", geocoder.getCountry(-98.22654,-256.35487).map(Country::continent).get());
            assertEquals("AS", geocoder.getCountry(-99.57824,98.40008).map(Country::continent).get());
        });
        assertDoesNotThrow( () -> {
            assertEquals("AS", geocoder.getCountry(7.88481,98.40008).map(Country::continent).get());

        });
    }

    @Test
    void TestfindcontinentFunc()
    {
        assertThrows(NoSuchElementException.class , ()-> {
            assertEquals("NA", geocoder.getCountry(0,0).map(Country::continent).get());
    });
        assertDoesNotThrow( () -> {
            assertEquals("NA", geocoder.getCountry(18.21704,-63.05783).map(Country::continent).get());
            assertEquals("SA", geocoder.getCountry(-3.46222,-44.87056).map(Country::continent).get());
            assertEquals("EU", geocoder.getCountry(42.50729,1.53414).map(Country::continent).get());
            assertEquals("AS", geocoder.getCountry(25.56473,55.55517).map(Country::continent).get());
            assertEquals("AF", geocoder.getCountry(8.88649,2.59753).map(Country::continent).get());
            assertEquals("OC", geocoder.getCountry(-31.89578,115.76431).map(Country::continent).get());
            assertEquals("AN", geocoder.getCountry(-54.28111,-36.5092).map(Country::continent).get());
        });
    }

    @Test
    //Test9: Check if the number of population given by the country match the info in geo data
    void testPopulationInt()
    {
        int thPopulation = 67089500;
        assertThrows(NoSuchElementException.class, ()->{
            assertEquals(thPopulation, geocoder.getCountry(17.711150,250.34277).map(Country::population).get());
            assertEquals(thPopulation, geocoder.getCountry(-95.72343,104.411472).map(Country::population).get());
            assertEquals(thPopulation, geocoder.getCountry(-95.72343,250.34277).map(Country::population).get());

        });

        assertDoesNotThrow( ()->{
            assertEquals(thPopulation, geocoder.getCountry(17.711150,104.411472).map(Country::population).get());

        });
    }

    @Test
    void testPopulationFunc()
    {
        int anPopulation = 13254;
        int brPopulation = 201103330;
        int andPopulation = 84000;
        int aePopulation = 4975593;
        int bePopulation = 9056010;
        int auPopulation = 21515754;
        int sgPopulation = 30;

        assertThrows(NoSuchElementException.class, ()->{
            assertEquals(anPopulation, geocoder.getCountry(0,0).map(Country::population).get());
            assertEquals(brPopulation, geocoder.getCountry(0,1).map(Country::population).get());
            assertEquals(andPopulation, geocoder.getCountry(0,2).map(Country::population).get());
            assertEquals(aePopulation, geocoder.getCountry(0,3).map(Country::population).get());
            assertEquals(bePopulation, geocoder.getCountry(0,4).map(Country::population).get());
            assertEquals(auPopulation, geocoder.getCountry(0,5).map(Country::population).get());
            assertEquals(sgPopulation, geocoder.getCountry(0,6).map(Country::population).get());
        });

        assertDoesNotThrow( ()->{
            assertEquals(anPopulation, geocoder.getCountry(18.21704,-63.05783).map(Country::population).get());
            assertEquals(brPopulation, geocoder.getCountry(-3.46222,-44.87056).map(Country::population).get());
            assertEquals(andPopulation, geocoder.getCountry(42.50729,1.53414).map(Country::population).get());
            assertEquals(aePopulation, geocoder.getCountry(25.56473,55.55517).map(Country::population).get());
            assertEquals(bePopulation, geocoder.getCountry(8.88649,2.59753).map(Country::population).get());
            assertEquals(auPopulation, geocoder.getCountry(-31.89578,115.76431).map(Country::population).get());
            assertEquals(sgPopulation, geocoder.getCountry(-54.28111,-36.5092).map(Country::population).get());
        });
    }

    //Test10: Check if the number of area given by the country match the info in geo data
    @Test
    void testAreaInt()
    {
        int thArea = 514000;
        assertThrows(NoSuchElementException.class, ()->{
            assertEquals(thArea, geocoder.getCountry(17.711150,250.34277).map(Country::area).get());
            assertEquals(thArea, geocoder.getCountry(-95.72343,104.411472).map(Country::area).get());
            assertEquals(thArea, geocoder.getCountry(-98.125167,250.34277).map(Country::area).get());
        });

        assertDoesNotThrow( ()->{
            assertEquals(thArea, geocoder.getCountry(17.711150,104.411472).map(Country::area).get());
        });
    }

    @Test
    void testAreaFunc()
    {
        int thArea = 514000;
        assertThrows(NoSuchElementException.class, ()->{
            assertEquals(thArea, geocoder.getCountry(17.711150,250.34277).map(Country::area).get());
        });

        assertDoesNotThrow( ()->{
            assertEquals(thArea, geocoder.getCountry(17.711150,104.411472).map(Country::area).get());
        });
    }
}
