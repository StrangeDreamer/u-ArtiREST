(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('BusinessRuleDeleteController',BusinessRuleDeleteController);

    BusinessRuleDeleteController.$inject = ['$uibModalInstance', 'entity', 'BusinessRule'];

    function BusinessRuleDeleteController($uibModalInstance, entity, BusinessRule) {
        var vm = this;

        vm.businessRule = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            BusinessRule.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
