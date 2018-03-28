# RTL_WITHOUT_GPS

### Automatic RTL (FailSafe for GPS)
The current system is based on the ideology that the GPS is at a good state during take off and failure occurs during mission. As long as the GPS is working well the GPS coordinates for each waypoint is stored, along with the velocity while traversing between each way point in arrays. Whenever GPS failure occurs, the GPS coordinates just before the failure is  saved and using it and the stored coordinates of the recently traversed waypoint, distance is calculated. Similarly, using the saved velocity and sending command to move the drone in reverse velocity we can make the drone travel the route in reverse order thereby reaching the home position. The major problem in the current system has been the duration for which the velocity commands are to be given, as it is not so    precise. Similarly, there is a need to calculate the bearing angle which will be big help to improve the system. Moreover, wind conditions has not been considered as of yet. 

#### To Run SITL
The following three commands need to be run:
* Running the dronekit-sitl
```
dronekit-sitl copter --home=27.7103049,85.3221333,1400,60
``` 
copter/plane, location, altitude, heading


* Running mavproxy to assign master and output ports for QGround/Mission_planner and dronekit code
```
mavproxy.py --master tcp:127.0.0.1:5760 --sitl 127.0.0.1:5501 --out 192.168.1.130:14550 --out 127.0.0.1:14551
```
Here master is generally, 127.0.0.1:5760 which is provided by the dronekit-sitl, the out commands are for the listeners. The ip address of the workstation is used by mission planner/Q Ground and port is as per user's choice. Similarly, for the dronekit program the ip and port is as user's choice.


* Finally Run the python code providing the ip and port to be connected
```
python simple_goto.py --connect 127.0.0.1:14551
```
### Program Description
* Initially the program stores the gps coordinates of home.
* The xvel, yvel and gps coordinates are stored in array at each waypoint, until the failsafe is activated inside a loop.
* using the gps coordinate of each waypoint the distance between each waypoint is calculated and stored.
* the required time for command ned_vel is calculated using the velocity and distance
* the command ned_vel is applied with velocity in just the opposite direction of stored value for the calculated amount of time.
