(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('ProcessDeleteController',ProcessDeleteController);

    ProcessDeleteController.$inject = ['$uibModalInstance', 'entity', 'Process'];

    function ProcessDeleteController($uibModalInstance, entity, Process) {
        var vm = this;

        vm.process = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Process.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
