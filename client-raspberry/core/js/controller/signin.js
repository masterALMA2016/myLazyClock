var controller = angular.module('myLazyClock.controller.signin', []);

controller.controller('myLazyClock.controller.signin', ['$scope', '$localStorage', '$interval', '$state', 'GApi', 'hotkeys',
    function signinCtl($scope, $localStorage, $interval, $state, GApi, hotkeys) {
        var interval;

        $scope.reload = function() {
            if ($localStorage.alarmClockId != undefined)
                checkLink($localStorage.alarmClockId);
        }

        hotkeys.add({
            combo: 'r',
            description: 'reload all',
            callback: $scope.reload
        });

        var checkLink = function(id) {
                GApi.execute('myLazyClock', 'alarmClock.item', {alarmClockId: id}).then( function(resp) {
                        if(resp.user != undefined) {
                            $interval.cancel(interval);
                            $state.go('webapp.home');
                        }
                    }, function(resp) {
                        if(resp.code = 404) {
                            $interval.cancel(interval);
                            $localStorage.alarmClockId = undefined;
                            check();
                        }
                    });
            }

    	var goToHome = function(id) {
    		checkLink(id);
    		interval = $interval(function() {
				checkLink(id);
			}, 60000);
    	}

        var generate = function() {
                GApi.execute('myLazyClock', 'alarmClock.generate').then( function(resp) {
                    $localStorage.alarmClockId = resp.id;
                    $scope.alarmClockId = resp.id;
                    goToHome($localStorage.alarmClockId);
                }, function() {
                    $timeout(generate, 4000);
                });
        }

        var check = function() {
            if($localStorage.alarmClockId == undefined) {
                generate();
            } else {
                $scope.alarmClockId = $localStorage.alarmClockId;
                goToHome($localStorage.alarmClockId);
            }
        }
    	
        check();
        
    }
])