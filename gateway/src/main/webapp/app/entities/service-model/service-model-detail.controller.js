(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('ServiceModelDetailController', ServiceModelDetailController);

    ServiceModelDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ServiceModel'];

    function ServiceModelDetailController($scope, $rootScope, $stateParams, previousState, entity, ServiceModel) {
        var vm = this;

        vm.serviceModel = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('gatewayApp:serviceModelUpdate', function(event, result) {
            vm.serviceModel = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
