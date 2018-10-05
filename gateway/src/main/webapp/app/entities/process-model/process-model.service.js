(function() {
    'use strict';
    angular
        .module('gatewayApp')
        .factory('ProcessModel', ProcessModel);

    ProcessModel.$inject = ['$resource', 'DateUtils'];

    function ProcessModel ($resource, DateUtils) {
        var resourceUrl =  'artifactmodel/' + 'api/process-models/:id';

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
