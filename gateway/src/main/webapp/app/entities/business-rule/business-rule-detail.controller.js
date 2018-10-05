(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('BusinessRuleDetailController', BusinessRuleDetailController);

    BusinessRuleDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'BusinessRule'];

    function BusinessRuleDetailController($scope, $rootScope, $stateParams, previousState, entity, BusinessRule) {
        var vm = this;

        vm.businessRule = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('gatewayApp:businessRuleUpdate', function(event, result) {
            vm.businessRule = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
