(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('ServiceModelDialogController', ServiceModelDialogController);

    ServiceModelDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ServiceModel'];

    function ServiceModelDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ServiceModel) {
        var vm = this;

        vm.serviceModel = entity;
        vm.clear = clear;
        vm.save = save;

        $scope.inputParams = '';
        if(vm.serviceModel.inputParams){
            $scope.inputParams = vm.serviceModel.inputParams.join(',');
        }
        $scope.allowedMethods = ["GET", "POST", "PUT", "DELETE"];
        $scope.allowedServiceTypes = ['HUMAN_TASK','INVOKE_SERVICE'];

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if($scope.inputParams){
                vm.serviceModel.inputParams = $scope.inputParams.split(',');
            } else {
                vm.serviceModel.inputParams = [];
            }
            if (vm.serviceModel.id !== null) {
                ServiceModel.update(vm.serviceModel, onSaveSuccess, onSaveError);
            } else {
                ServiceModel.save(vm.serviceModel, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('gatewayApp:serviceModelUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
