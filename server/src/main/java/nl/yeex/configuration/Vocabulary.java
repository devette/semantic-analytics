//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package nl.yeex.configuration;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public class Vocabulary {
    private static final Model m = ModelFactory.createDefaultModel();
    public static final String NS = "http://vocabulary.yeex.nl/configuration/v1#";
    public static final Resource NAMESPACE;
    public static final Resource application;
    // name should be switch but is that is a reserved keyword.
    public static final Resource aSwitch;
    public static final Resource ruleSet;
    public static final Resource account;
    public static final Resource accountGroup;

    public static final Property name;
    public static final Property iban;
    public static final Property bic;
    public static final Property hasRuleSet;
    public static final Property hasAccount;
    public static final Property hasAccountGroup;
    public static final Property effectiveFrom;
    public static final Property effectiveUntil;
    public static final Property matchesAccountPattern;

    public Vocabulary() {
    }

    public static String getURI() {
        return "http://vocabulary.yeex.nl/configuration/v1#";
    }

    static {
        NAMESPACE = m.createResource(NS);
        application = m.createResource(NS + "Application");
        aSwitch = m.createResource(NS + "Switch");
        ruleSet = m.createResource(NS + "RuleSet");
        account = m.createResource(NS + "Account");
        accountGroup = m.createResource(NS + "AccountGroup");

        name = m.createProperty(NS + "name");
        hasRuleSet = m.createProperty(NS + "hasRuleSet");
        hasAccount = m.createProperty(NS + "hasAccount");
        hasAccountGroup = m.createProperty(NS + "hasAccountGroup");
        effectiveFrom = m.createProperty(NS + "effectiveFrom");
        effectiveUntil = m.createProperty(NS + "effectiveUntil");
        matchesAccountPattern = m.createProperty(NS + "matchesAccountPattern");
        iban = m.createProperty(NS + "iban");
        bic = m.createProperty(NS + "bic");
    }
}
