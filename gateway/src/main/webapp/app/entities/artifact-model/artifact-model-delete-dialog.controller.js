(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('ArtifactModelDeleteController',ArtifactModelDeleteController);

    ArtifactModelDeleteController.$inject = ['$uibModalInstance', 'entity', 'ArtifactModel'];

    function ArtifactModelDeleteController($uibModalInstance, entity, ArtifactModel) {
        var vm = this;

        vm.artifactModel = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ArtifactModel.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
