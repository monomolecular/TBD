package tests

import spock.lang.Specification
import spock.lang.Unroll;

/**
 * In Spock, you don't have tests, you have specifications. These are normal Groovy classes that extend the
 * Specifications class, which is actually a JUnit class. Your class contains a set of specifications, represented by
 * methods with funny-method-names-in-quotes™. The funny-method-names-in-quotes™ take advantage of some Groovy magic
 * to let you express your requirements in a very readable form. And since these classes are derived from JUnit, you
 * can run them from within your IDE like a normal Groovy unit test, and they produce standard JUnit reports, which is
 * nice for CI servers.
 *
 * To setup the dependencies correctly...
 * Open File->Project Structure,Click Modules->Dependences, and add:
 *    junit.jar
 *    junit-4.12.jar
 *    groovy-all-2.3.9.jar
 *    hamcrest-core-1.3.jar
 */
class ExampleSpockTests extends Specification {

    /**
     * Fixture Methods
     *
     * Fixture methods are responsible for setting up and cleaning up the environment in which feature methods are run.
     * Usually it's a good idea to use a fresh fixture for every feature method, which is what the setup() and
     * cleanup() methods are for. Occasionally it makes sense for feature methods to share a fixture, which is achieved
     * by using shared fields together with the setupSpec() and cleanupSpec() methods. All fixture methods are optional.
     *
     * Note: The setupSpec() and cleanupSpec() methods may not reference instance fields.
     **/

    /**
     * run before every feature method
     * - must be first
     * - must be the only
     * - no special semantics
     * - label is optional
     * - label given: is an alias
     */
    void setup() { }
    void setupSpec() { }    // run before the first feature method for expensive objects.

    /**
     * Feature Methods
     *
     * Feature methods are the heart of a specification. They describe the features (properties, aspects) that you
     * expect to find in the system under specification. By convention, feature methods are named with String literals.
     * Try to choose good names for your feature methods, and feel free to use any characters you like! * Spock approach to feature tests (specs):
     * 1) Setup the features fixture
     * 2) Provide the stimulus to the system
     * 3) Describes the response
     * 4) Clean up
     *
     * Spock specs utilize given:, when: and then: to express actions and expected outcomes. This structure is common in
     * Behaviour-Driven Development. You can make the intent clear by adding text descriptions after the when: and then:
     * labels. e.g. when: "I do something.", then: "The result should look like this other thing."
     *
     * Blocks include:
     * given: preconditions, data fixtures
     * when: actions that trigger some outcome
     * then: goes with when, makes assertions about outcome
     * expect: short alternative to when & then
     * and: eye-candy, divides other blocks
     * setup: alias for given
     * cleanup: post-conditions, housekeeping
     *
     * Conceptually, a feature method consists of four phases:
     * 1) Set up the feature's fixture
     * 2) Provide a stimulus to the system under specification
     * 3) Describe the response expected from the system
     * 4) Clean up the feature's fixture
     *
     * Whereas the first and last phases are optional, the stimulus and response phases are always present (except in
     * interacting feature methods), and may occur more than once.
     **/

    @Unroll
    def "computing the maximum of #a and #b is #c"() {
        expect:
        Math.max(a, b) == c

        where:
        a << [5,3,1]
        b << [1,9,1]
        c << [5,9,3]
    }

    @Unroll
    def "check Star Trek character name, #name, has length of #length"() {
        expect: name.size() == length

        where:
        name     | length
        "Spock"  | 5
        "Kirk"   | 5
        "Scotty" | 6
    }

    void cleanupSpec() { }
    void cleanup() { }      // run after the last feature method
}
