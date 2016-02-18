package uk.ac.cam.quebec.kgsearchwrapper;

/**
 * Testing for the KCConceptGenerator class
 *
 * @author tudor
 */
public class KGConceptGeneratorTest {

    public static void main(String[] args) {

        KGConceptGenerator generator = new KGConceptGenerator();

        String[] queries = new String[] {
                "donald trump", "taylor swift", "how old is barack obama",
                "chelsea", "cambridge", ""
        };

        for (String query : queries) {
            System.out.println("\n>>>> TEST: " + query + " <<<<\n");
            for (KGConcept concept : generator.getKGConcepts(query)) {
                System.out.println("\nnew concept: \n");
                System.out.println(concept.toString() + "\n");
            }
        }
    }

}
