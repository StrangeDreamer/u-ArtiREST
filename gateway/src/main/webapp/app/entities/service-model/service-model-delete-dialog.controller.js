(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('ServiceModelDeleteController',ServiceModelDeleteController);

    ServiceModelDeleteController.$inject = ['$uibModalInstance', 'entity', 'ServiceModel'];

    function ServiceModelDeleteController($uibModalInstance, entity, ServiceModel) {
        var vm = this;

        vm.serviceModel = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ServiceModel.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
