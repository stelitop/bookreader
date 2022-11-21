package bookreader.utils;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TextUtilsTest {

    private static TextUtils textUtils;
    
    @BeforeAll
    public static void before() {
        textUtils = new TextUtils();
    }
    
    @Test
    public void testLanguageDetectionBG() {
        String fullyBulgarianText = "Паяците, известни още като същински паяци, са разред безгръбначни хищни животни от клас Паякообразни. Характерни отличителни черти са наличието на осем крака, челюсти снабдени със зъби, отделящи отрова, паяжинна жлеза в задната част на коремчето, както и липсата на крила.";
        assertThat(textUtils.getLanguage(fullyBulgarianText)).isEqualTo("bg");
        String mostlyBulgarianText = "Паяците, известни още като същински паяци, are spineless predatory animals.";
        assertThat(textUtils.getLanguage(mostlyBulgarianText)).isEqualTo("bg");
    }

    @Test
    public void testLanguageDetectionEN() {
        String fullyEnglishText = "Spiders (order Araneae) are air-breathing arthropods that have eight legs, chelicerae with fangs generally able to inject venom,[2] and spinnerets that extrude silk.[3] They are the largest order of arachnids and rank seventh in total species diversity among all orders of organisms.";
        assertThat(textUtils.getLanguage(fullyEnglishText)).isEqualTo("en");
        String mostlyEnglishText = "Spiders (order Araneae) are air-breathing arthropods that имат 8 крака.";
        assertThat(textUtils.getLanguage(mostlyEnglishText)).isEqualTo("en");
    }
}