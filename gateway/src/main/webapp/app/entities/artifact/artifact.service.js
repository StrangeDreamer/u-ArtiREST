(function() {
    'use strict';
    angular
        .module('gatewayApp')
        .factory('Artifact', Artifact);

    Artifact.$inject = ['$resource', 'DateUtils'];

    function Artifact ($resource, DateUtils) {
        var resourceUrl =  'processes/' + 'api/artifacts/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.createdAt = DateUtils.convertDateTimeFromServer(data.createdAt);
                        data.updatedAt = DateUtils.convertDateTimeFromServer(data.updatedAt);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
