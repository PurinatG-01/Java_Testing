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
    // Check whether lat and lon can be found in geo data
    void TestErrorthrow()
    {

        assertThrows(NoSuchElementException.class, ()->{
            geocoder.getCountry(0,0).get();
        });

        assertDoesNotThrow( ()->{

            geocoder.getCountry(38.897529,-77.037128).get();

        });

    }

    @Test
    // Check if this lat & lon match the country name in geo data
    void Testfindcountryname()
    {

        assertEquals("Thailand", geocoder.getCountry(7.88481,98.40008).map(Country::name).get());
        assertEquals("United States", geocoder.getCountry(39.9512,-75.15923).map(Country::name).get());
        assertEquals("South Africa", geocoder.getCountry(-26.1625,27.8725).map(Country::name).get());

    }

    @Test
    //Check boundary of two adjacent country in geo data
    void Testadjacentcountryboundary()
    {

        assertEquals("Thailand", geocoder.getCountry(17.711150, 104.411472).map(Country::name).get());
        assertEquals("Laos", geocoder.getCountry(17.722720, 104.427701).map(Country::name).get());

    }

    @Test
    //Check boundary of continent in geo data
    void Testcountryboundary()
    {

        assertEquals("Australia", geocoder.getCountry(-34.704150, 116.932848).map(Country::name).get());

        assertThrows(NoSuchElementException.class, ()->{
            geocoder.getCountry(-35.053366, 116.873733).get();
        });

    }

    @Test
    //Check if this lat & lon match the country iso in geo data
    void Testcountryiso()
    {
        assertEquals("TH", geocoder.getCountry(7.88481,98.40008).map(Country::iso).get());
        assertEquals("US", geocoder.getCountry(39.9512,-75.15923).map(Country::iso).get());
        assertEquals("ZA", geocoder.getCountry(-26.1625,27.8725).map(Country::iso).get());

    }

    @Test
    //Check
    void Testminmaxlatlon()
    {
        assertThrows(NoSuchElementException.class, ()->{
            geocoder.getCountry(-91.00,181.00).get();
        });

        assertThrows(NoSuchElementException.class, ()->{
            geocoder.getCountry(-91.00,-181.00).get();
        });

        assertThrows(NoSuchElementException.class, ()->{
            geocoder.getCountry(91.00,-181.00).get();
        });

        assertThrows(NoSuchElementException.class, ()->{
            geocoder.getCountry(91.00,181.00).get();
        });

        assertThrows(NoSuchElementException.class, ()->{
            geocoder.getCountry(-91.00,27.8725).get();
        });

        assertThrows(NoSuchElementException.class, ()->{
            geocoder.getCountry(91.00,27.8725).get();
        });

        assertThrows(NoSuchElementException.class, ()->{
            geocoder.getCountry(7.88481,-181.00).get();
        });

        assertThrows(NoSuchElementException.class, ()->{
            geocoder.getCountry(7.88481,181.00).get();
        });

    }

    @Test
    //Check local languages for each country in geo data (locales ordered by the number of speakers)
    void Testlocales()
    {
        List<Locale> thlocales = Arrays.asList(
                new Locale("th"),
                new Locale("en")
        );

        assertEquals(thlocales, geocoder.getCountry(7.88481,98.40008).map(Country::locales).get());

        List<Locale> uklocales = Arrays.asList(
                new Locale("en", "GB"),
                new Locale("cy", "GB"),
                new Locale("gd")
        );

        assertEquals(uklocales, geocoder.getCountry(51.507222, -0.1275).map(Country::locales).get());

    }

    @Test
    //Check if this lat & lon match the country continent in geo data
    void Testfindcontinent()
    {

        assertEquals("AS", geocoder.getCountry(7.88481,98.40008).map(Country::continent).get());
        assertEquals("NA", geocoder.getCountry(39.9512,-75.15923).map(Country::continent).get());
        assertEquals("AF", geocoder.getCountry(-26.1625,27.8725).map(Country::continent).get());
    }



}
