(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('ArtifactDeleteController',ArtifactDeleteController);

    ArtifactDeleteController.$inject = ['$uibModalInstance', 'entity', 'Artifact'];

    function ArtifactDeleteController($uibModalInstance, entity, Artifact) {
        var vm = this;

        vm.artifact = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Artifact.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
