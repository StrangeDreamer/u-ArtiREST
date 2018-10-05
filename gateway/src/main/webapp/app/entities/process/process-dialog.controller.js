(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('ProcessDialogController', ProcessDialogController);

    ProcessDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Process'];

    function ProcessDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Process) {
        var vm = this;

        vm.process = entity;
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
            if (vm.process.id !== null) {
                Process.update(vm.process, onSaveSuccess, onSaveError);
            } else {
                Process.save(vm.process, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('gatewayApp:processUpdate', result);
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
