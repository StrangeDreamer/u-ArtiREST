(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('ArtifactDialogController', ArtifactDialogController);

    ArtifactDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Artifact'];

    function ArtifactDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Artifact) {
        var vm = this;

        vm.artifact = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.artifact.id !== null) {
                Artifact.update(vm.artifact, onSaveSuccess, onSaveError);
            } else {
                Artifact.save(vm.artifact, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('gatewayApp:artifactUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.createdAt = false;
        vm.datePickerOpenStatus.updatedAt = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
