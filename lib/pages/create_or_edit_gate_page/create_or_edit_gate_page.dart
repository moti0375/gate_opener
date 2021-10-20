import 'dart:async';
import 'dart:collection';

import 'package:flutter/material.dart';
import 'package:gate_opener/res/colors.dart';
import 'package:gate_opener/widgets/designed_button.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:location/location.dart';

class CreateOrEditGatePage extends StatefulWidget {
  const CreateOrEditGatePage({Key? key}) : super(key: key);

  @override
  _CreateOrEditGatePageState createState() => _CreateOrEditGatePageState();
}

class _CreateOrEditGatePageState extends State<CreateOrEditGatePage> {

  Location location = new Location();
  bool _serviceEnabled = false;
  Completer<GoogleMapController> _controller = Completer();
  late GoogleMapController _googleMapController;
  double currentZoom = 15;

  CameraPosition _kGooglePlex = CameraPosition(
    target: LatLng(0, 0),
    zoom: 15,
  );

  Set<Marker> markers = HashSet();

  @override
  void initState() {
    super.initState();
    _initializeMap();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Create Gate"),
        elevation: 0,
      ),
      body: Padding(
        padding: EdgeInsets.all(24),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Expanded(
              child: ClipRRect(
                borderRadius: BorderRadius.all(Radius.circular(16)),
                child: GoogleMap(
                  markers: markers,
                  onTap: _onMapClicked,
                  mapType: MapType.normal,
                  myLocationButtonEnabled: true,
                  myLocationEnabled: true,
                  initialCameraPosition: _kGooglePlex,
                  onMapCreated: (GoogleMapController controller) {
                    _controller.complete(controller);
                    _googleMapController = controller;
                  },
                  onCameraMove: _onCameraMoved,
                ),
              ),
            ),
            Expanded(child: _createFormSection()),
            DesignedButton(
              color: AppColors.shareButton,
              height: 60,
              text: "Select",
              onPressed: (){},
            )
          ],
        ),
      ),
    );
  }

  void _initializeMap() async {
    _serviceEnabled = await location.serviceEnabled();
    if(_serviceEnabled){
      LocationData locationData = await location.getLocation();
      _moveMapToPosition(LatLng(locationData.latitude!, locationData.longitude!), currentZoom);
    }
  }

  void _onMapClicked(LatLng latLng) {
    setState(() {
      Marker marker = Marker(markerId: MarkerId("GateMarker"), position: latLng);
      markers.clear();
      markers.add(marker);
    });
    _moveMapToPosition(latLng, currentZoom);
  }

  void _moveMapToPosition(LatLng position, zoom){
    CameraPosition camPosition = CameraPosition(target: position, zoom: zoom);
    _googleMapController.animateCamera(CameraUpdate.newCameraPosition(camPosition));
  }

  void _onCameraMoved(CameraPosition position) {
    setState(() {
      currentZoom = position.zoom;
    });
  }

  Widget _createFormSection() {
    return Column(
      mainAxisSize: MainAxisSize.max,
      crossAxisAlignment: CrossAxisAlignment.stretch,
      mainAxisAlignment: MainAxisAlignment.start,
      children: [

      ],
    );
  }
}
