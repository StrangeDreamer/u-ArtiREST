(function() {
    'use strict';

    angular
        .module('gatewayApp')
        .controller('ArtifactModelDialogController', ArtifactModelDialogController);

    ArtifactModelDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ArtifactModel'];

    function ArtifactModelDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ArtifactModel) {
        var vm = this;

        vm.artifactModel = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $scope.attribute = {name: '', comment: '', type: 'String'};
        $scope.state = {name: '', comment: '', type: 'NORMAL', nextStates: ''};

        $scope.attributeTypes = ["String", "Integer", "Double", "Text"];
        $scope.stateTypes = ['NORMAL', 'START', 'FINAL'];

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.artifactModel.id !== null) {
                ArtifactModel.update(vm.artifactModel, onSaveSuccess, onSaveError);
            } else {
                ArtifactModel.save(vm.artifactModel, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('gatewayApp:artifactModelUpdate', result);
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

        $scope.addAttribute = function(attribute){
            $scope.artifactModel.attributes.push(attribute);
            $scope.attribute = {name: '', comment: '', type: 'String'};
        };

        $scope.addState = function(state){
            state.nextStates = state.nextStates.split(',');
            $scope.artifactModel.states.push(state);
            $scope.state = {name: '', comment: '', type: 'NORMAL', nextStates: ''};
        };

        $scope.removeAttribute = function(attribute){
            var index = $scope.artifactModel.attributes.indexOf(attribute);
            if(index>=0){
                $scope.artifactModel.attributes.splice(index, 1);
            }
        };


        $scope.removeState = function(state){
            var index = $scope.artifactModel.states.indexOf(state);
            if(index>=0){
                $scope.artifactModel.states.splice(index, 1);
            }
        };
    }
})();
