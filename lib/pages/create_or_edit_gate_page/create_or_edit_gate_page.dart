import 'dart:async';
import 'dart:collection';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
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
      resizeToAvoidBottomInset: true,
      body: Padding(
        padding: EdgeInsets.all(24),
        child: LayoutBuilder(
          builder: (context, viewportConstraints) => SingleChildScrollView(
            physics: FixedExtentScrollPhysics(),
            child: ConstrainedBox(
              constraints: BoxConstraints(
                  minHeight: viewportConstraints.minHeight,
                  maxHeight: viewportConstraints.maxHeight
              ),
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
                    onPressed: () {},
                  )
                ],
              ),
            ),
          ) ,
        ),
      ),
    );
  }

  void _initializeMap() async {
    _serviceEnabled = await location.serviceEnabled();
    if (_serviceEnabled) {
      LocationData locationData = await location.getLocation();
      _moveMapToPosition(
          LatLng(locationData.latitude!, locationData.longitude!), currentZoom);
    }
  }

  void _onMapClicked(LatLng latLng) {
    setState(() {
      Marker marker =
          Marker(markerId: MarkerId("GateMarker"), position: latLng);
      markers.clear();
      markers.add(marker);
    });
    _moveMapToPosition(latLng, currentZoom);
  }

  void _moveMapToPosition(LatLng position, zoom) {
    CameraPosition camPosition = CameraPosition(target: position, zoom: zoom);
    _googleMapController
        .moveCamera(CameraUpdate.newCameraPosition(camPosition));
  }

  void _onCameraMoved(CameraPosition position) {
    setState(() {
      currentZoom = position.zoom;
    });
  }

  Widget _createFormSection() {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: Column(
        mainAxisSize: MainAxisSize.max,
        crossAxisAlignment: CrossAxisAlignment.stretch,
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          Row(
            mainAxisSize: MainAxisSize.min,
            mainAxisAlignment: MainAxisAlignment.spaceEvenly,
            children: [
              Expanded(
                child: Container(
                  color: Colors.grey,
                    alignment: Alignment.center,
                    child: Text(
                       "36.656",
                      style: Theme.of(context).textTheme.headline3,
                      maxLines: 1,
                      textAlign: TextAlign.center,
                      overflow: TextOverflow.clip,
                    )),
              ),
              VerticalDivider(thickness: 4, color: Colors.blue,),
              Expanded(
                child: Container(
                  color: Colors.grey,
                  alignment: Alignment.center,
                  child: Text(
                  "36.65",
                  style: Theme.of(context).textTheme.headline3,
                  maxLines: 1,
                  textAlign: TextAlign.center,
                    overflow: TextOverflow.clip,
                ),
                ),
              )
            ],
          ),
          SizedBox(height: 16,),
          TextField(
            style: Theme.of(context).textTheme.headline6,
            decoration: InputDecoration(
              border: OutlineInputBorder(),
              enabledBorder: OutlineInputBorder(),
              focusedBorder: OutlineInputBorder(),
              labelStyle: Theme.of(context).textTheme.bodyText1,
              labelText: "Name"
            ),
          ),
          SizedBox(height: 16,),
          TextField(
            style: Theme.of(context).textTheme.headline6,
            keyboardType: TextInputType.phone,
            decoration: InputDecoration(
                border: OutlineInputBorder(),
                enabledBorder: OutlineInputBorder(),
                focusedBorder: OutlineInputBorder(),
                labelStyle: Theme.of(context).textTheme.bodyText1,
                labelText: "Phone Number"
            ),
          )
        ],
      ),
    );
  }
}
