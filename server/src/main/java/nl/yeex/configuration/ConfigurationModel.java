package nl.yeex.configuration;


import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;

import java.time.format.DateTimeFormatter;

import static nl.yeex.configuration.Vocabulary.*;

public class ConfigurationModel {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static Model create()  {
        Model model = ModelFactory.createDefaultModel();

        final Resource friendsAndFamily = model.createResource(accountGroup)
                .addProperty(name, "friendsAndFamily")
                .addProperty(RDFS.label, model.createLiteral("Vrienden en Familie", "nl"))
                .addProperty(RDFS.label, model.createLiteral("Friends and Family", "en"))
                .addProperty(hasAccount,
                        model.createResource(account)
                                .addLiteral(bic, "ABNANL2A")
                                .addLiteral(iban, "NL94ABNA0123456789"))
                .addProperty(hasAccount,
                        model.createResource(account)
                                .addLiteral(bic, "RABONL2U")
                                .addLiteral(iban, "NL94RABO0123456789"));

        final Resource pilotAccountGroup = model.createResource(accountGroup)
                .addProperty(name, "pilotAccountGroup")
                .addProperty(RDFS.label, model.createLiteral("Pilot Groep", "nl"))
                .addProperty(RDFS.label, model.createLiteral("Pilot Group", "en"))
                .addLiteral(matchesAccountPattern, "NL94RABO0123456[0-9]{2}");


        model.createResource(aSwitch)
                .addProperty(name, "useOldIPO")
                .addProperty(RDFS.label, model.createLiteral("Gebruik oude IPO", "nl"))
                .addProperty(RDFS.label, model.createLiteral("Use Old IPO", "en"))
                .addProperty(hasRuleSet,
                        model.createResource(ruleSet)
                                .addLiteral(effectiveFrom, getDate(model, "2019-01-01T00:00:00"))
                                .addLiteral(effectiveUntil, getDate(model, "2020-01-01T00:00:00")));

        model.createResource(aSwitch)
                .addProperty(name, "useNewIPO")
                .addProperty(RDFS.label, model.createLiteral("Gebruik nieuwe IPO", "nl"))
                .addProperty(RDFS.label, model.createLiteral("Use New IPO", "en"))
                .addProperty(hasRuleSet,
                        model.createResource(ruleSet)
                                .addLiteral(effectiveFrom, getDate(model, "2019-01-01T00:00:00"))
                                .addLiteral(effectiveUntil, getDate(model, "2020-01-01T00:00:00"))
                .addProperty(hasAccountGroup, friendsAndFamily));

        model.createResource(aSwitch)
                .addProperty(name, "useNewSPO")
                .addProperty(RDFS.label, model.createLiteral("Gebruik nieuwe SPO", "nl"))
                .addProperty(RDFS.label, model.createLiteral("Use New SPO", "en"))
                .addProperty(hasRuleSet,
                        model.createResource(ruleSet)
                                .addLiteral(effectiveFrom, getDate(model, "2019-01-01T00:00:00"))
                                .addLiteral(effectiveUntil, getDate(model, "2020-01-01T00:00:00"))
                                .addProperty(hasAccountGroup, pilotAccountGroup));

        return model;
    }

    private static Literal getDate(Model model, String date) {
        //LocalDateTime.parse(date).format(DATE_TIME_FORMATTER)
        return model.createTypedLiteral(date, XSDDatatype.XSDdateTime);
    }

}
