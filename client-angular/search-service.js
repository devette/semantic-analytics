(function() {

    'use strict';

    /* eslint-disable angular/no-service-method */

    // Module definition, note the dependency.
    angular.module('facetApp', ['seco.facetedSearch'])

    /*
     * DBpedia service
     * Handles SPARQL queries and defines facet configurations.
     */
    .service('searchService', searchService);

    /* @ngInject */
    function searchService(FacetResultHandler) {

        /* Public API */

        // Get the results from DBpedia based on the facet selections.
        this.getResults = getResults;
        // Get the facet definitions.
        this.getFacets = getFacets;
        // Get the facet options.
        this.getFacetOptions = getFacetOptions;

        /* Implementation */

        // Facet definitions
        // 'facetId' is a "friendly" identifier for the facet,
        //  and should be unique within the set of facets.
        // 'predicate' is the property that defines the facet (can also be
        //  a property path, for example).
        // 'name' is the title of the facet to show to the user.
        // If 'enabled' is not true, the facet will be disabled by default.
        var facets = {
            meldingtekst: {
                    facetId: 'meldingtekst',
                    predicate:'<http://afm.nl/register/short#meldingtekst>',
                    enabled: true,
                    chart: false,
                    name: 'Melding tekst'
             },
            meldingsplichtige: {
                facetId: 'meldingsplichtige',
                predicate:'<http://afm.nl/register/short#meldingsplichtige>',
                enabled: true,
                chart: true,
                name: 'Meldingsplichtige'
            },
            uitgevendeInstelling: {
                facetId: 'uitgevendeInstelling',
                predicate: '<http://afm.nl/register/short#uitgevende-instelling>',
                enabled: true,
                chart: true,
                name: 'Uitgevende instelling'
            },
            isin: {
                facetId: 'isin',
                predicate:'<http://afm.nl/register/short#isin>',
                enabled: true,
                chart: true,
                name: 'ISIN'
            },
            nettoShortpositie: {
                facetId: 'nettoShortpositie',
                predicate: '<http://afm.nl/register/short#netto-shortpositie>',
                enabled: true,
                chart: true,
                name: 'Netto shortpositie'
            },
//            positiedatum: {
//                facetId: 'positiedatum',
//                predicate: '<http://afm.nl/register/short#positiedatum>',
//                enabled: true,
//                chart: true,
//                name: 'Positie datum'
//            },
            positiedatum: {
                name: 'Positie datum',
                facetId: 'positiedatum',
                startPredicate: '<http://afm.nl/register/short#positiedatum>',
                endPredicate: '<http://afm.nl/register/short#positiedatum>',
                min: '2014-01-01',
                max: '2018-12-31',
                enabled: true
            },
            positiejaar: {
                facetId: 'positiejaar',
                predicate: '<http://afm.nl/register/short#positiejaar>',
                enabled: true,
                chart: true,
                name: 'Positie jaar'
            },
            positiemaand: {
                facetId: 'positiemaand',
                predicate: '<http://afm.nl/register/short#positiemaand>',
                enabled: true,
                chart: true,
                name: 'Positie maand'
            }
        };

        var endpointUrl = 'http://localhost:8080/short/sparql';

        // We are building a faceted search for writers.
        var rdfClass = '<http://afm.nl/register/short#vermelding>';

        // The facet configuration also accept a 'constraint' option.
        // The value should be a valid SPARQL pattern.
        // One could restrict the results further, e.g., to writers in the
        // science fiction genre by using the 'constraint' option:
        //
        // var constraint = '?id <http://dbpedia.org/ontology/genre> <http://dbpedia.org/resource/Science_fiction> .';
        //
        // Note that the variable representing a result in the constraint should be "?id".
        //
        // 'rdfClass' is just a shorthand constraint for '?id a <rdfClass> .'
        // Both rdfClass and constraint are optional, but you should define at least
        // one of them, or you might get bad results when there are no facet selections.
        var facetOptions = {
            endpointUrl: endpointUrl, // required
            rdfClass: rdfClass, // optional
            usePost: false,
            // constraint: constraint, // optional, not used in this demo
            preferredLang : 'en' // required
        };

        var prefixes =
        ' PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>' +
        ' PREFIX afm: <http://afm.nl/register/short#>';

        // This is the result query, with <RESULT_SET> as a placeholder for
        // the result set subquery that is formed from the facet selections.
        // The variable names used in the query will be the property names of
        // the resulting mapped objects.
        // Note that ?id is the variable used for the result resource here,
        // as in the constraint option.
        // Variable names with a '__' (double underscore) in them will results in
        // an object. I.e. here ?work__id, ?work__label, and ?work__link will be
        // combined into an object:
        // writer.work = { id: '[work id]', label: '[work label]', link: '[work link]' }
        var queryTemplate =
        ' SELECT * WHERE {' +
        '  <RESULT_SET> ' +
        '  OPTIONAL { '+
        '   ?id afm:meldingtekst ?meldingtekst . ' +
        '  }' +
        '  OPTIONAL { '+
        '   ?id afm:meldingsplichtige ?meldingsplichtige . ' +
        '  }' +
        '  OPTIONAL { ' +
        '   ?id afm:uitgevende-instelling ?uitgevendeInstelling . ' +
        '  }' +
        '  OPTIONAL { ' +
        '   ?id afm:isin ?isin . ' +
        '  }' +
        '  OPTIONAL { ' +
        '   ?id afm:netto-shortpositie ?nettoShortpositie . ' +
        '  }' +
        '  OPTIONAL { ' +
        '   ?id afm:positiedatum ?positiedatum . ' +
        '  }' +
        '  OPTIONAL { ' +
        '   ?id afm:positiejaar ?positiejaar . ' +
        '  }' +
        '  OPTIONAL { ' +
        '   ?id afm:positiemaand ?positiemaand . ' +
        '  }' +
        ' }';

        var resultOptions = {
            prefixes: prefixes, // required if the queryTemplate uses prefixes
            queryTemplate: queryTemplate, // required
            resultsPerPage: 10, // optional (default is 10)
            pagesPerQuery: 1, // optional (default is 1)
            usePost: false,
            paging: true // optional (default is true), if true, enable paging of the results
        };

        // FacetResultHandler is a service that queries the endpoint with
        // the query and maps the results to objects.
        var resultHandler = new FacetResultHandler(endpointUrl, resultOptions);

        // This function receives the facet selections from the controller
        // and gets the results from DBpedia.
        // Returns a promise.
        function getResults(facetSelections) {
            // If there are variables used in the constraint option (see above),
            // you can also give getResults another parameter that is the sort
            // order of the results (as a valid SPARQL ORDER BY sequence, e.g. "?id").
            // The results are sorted by URI (?id) by default.
            return resultHandler.getResults(facetSelections).then(function(pager) {
                // We'll also query for the total number of results, and load the
                // first page of results.
                return pager.getTotalCount().then(function(count) {
                    pager.totalCount = count;
                    return pager.getPage(0);
                }).then(function() {
                    return pager;
                });
            });
        }

        // Getter for the facet definitions.
        function getFacets() {
            return facets;
        }

        // Getter for the facet options.
        function getFacetOptions() {
            return facetOptions;
        }
    }
})();
