import 'dart:async';
import 'dart:collection';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:gate_opener/data/model/gate.dart';
import 'package:gate_opener/data/repository/gate_opener_repository.dart';
import 'package:gate_opener/di/locator.dart';
import 'package:gate_opener/res/colors.dart';
import 'package:gate_opener/utils/string_utils.dart';
import 'package:gate_opener/widgets/app_text_view.dart';
import 'package:gate_opener/widgets/custom_dialog.dart';
import 'package:gate_opener/widgets/designed_button.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:location/location.dart';
import 'package:gate_opener/pages/create_or_edit_gate_page/create_or_edit_gate_store.dart';
import 'package:provider/provider.dart';
import 'package:mobx/mobx.dart';

class CreateOrEditGatePage extends StatefulWidget {
  final CreateOrEditStore store;

  const CreateOrEditGatePage({Key? key, required this.store}) : super(key: key);

  @override
  _CreateOrEditGatePageState createState() => _CreateOrEditGatePageState();

  static Widget create({Gate? initialGate}) {
    return Provider<CreateOrEditStore>(
      create: (_) => CreateOrEditStore(locator<GateOpenerRepository>(), initialGate),
      child: Consumer<CreateOrEditStore>(
          builder: (context, store, child) => CreateOrEditGatePage(
                store: store,
              )),
    );
  }
}

class _CreateOrEditGatePageState extends State<CreateOrEditGatePage> {
  Location location = new Location();
  bool _serviceEnabled = false;
  Completer<GoogleMapController> _controller = Completer();
  late GoogleMapController _googleMapController;
  double currentZoom = 15;
  ReactionDisposer? mobxDispose;
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
  void didChangeDependencies() {
    super.didChangeDependencies();
    mobxDispose ??=
        reaction((_) => widget.store.createOrEditAction, (CreateOrEditAction? value) => _handleActionChange(value));
  }

  @override
  void dispose() {
    super.dispose();
    mobxDispose?.call();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text("Create Gate"),
        elevation: 0,
      ),
      resizeToAvoidBottomInset: false,
      body: Padding(
        padding: const EdgeInsets.all(24.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            SizedBox(
              height: MediaQuery.of(context).size.height * 1 / 3,
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
            Expanded(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  _createFormSection(context),
                  Observer(
                    builder: (_) => DesignedButton(
                      color: AppColors.shareButton,
                      height: 60,
                      text: "Select",
                      onPressed: widget.store.formValid
                          ? () => widget.store.submit()
                          : null,
                    ),
                  )
                ],
              ),
            )
          ],
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
    widget.store.setLocationChanged(latLng);
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

  Widget _createFormSection(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: Column(
        mainAxisSize: MainAxisSize.max,
        crossAxisAlignment: CrossAxisAlignment.stretch,
        mainAxisAlignment: MainAxisAlignment.start,
        children: [
          Observer(
            builder: (_) => Row(
              mainAxisSize: MainAxisSize.min,
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                Expanded(
                  child: Container(
                      alignment: Alignment.center,
                      child: Text(
                        "${widget.store.location?.latitude ?? StringUtils.EMPTY_STRING}",
                        style: Theme.of(context).textTheme.headline3,
                        maxLines: 1,
                        textAlign: TextAlign.center,
                        overflow: TextOverflow.clip,
                      )),
                ),
                SizedBox(
                  height: 35,
                  child: VerticalDivider(
                    thickness: 4,
                  ),
                ),
                Expanded(
                  child: Container(
                    child: Text(
                      "${widget.store.location?.longitude ?? StringUtils.EMPTY_STRING}",
                      style: Theme.of(context).textTheme.headline3,
                      maxLines: 1,
                      textAlign: TextAlign.center,
                      overflow: TextOverflow.clip,
                    ),
                  ),
                )
              ],
            ),
          ),
          SizedBox(
            height: 16,
          ),
          AppTextView(
            text: widget.store.name,
            rightIcon: Icon(
              Icons.create,
              size: 50,
            ),
            style: Theme.of(context).textTheme.headline6,
            onRightIconClicked: () => widget.store.onSetNameClicked()
          ),
          SizedBox(
            height: 16,
          ),
          AppTextView(
            text: widget.store.phoneNumber,
            rightIcon: Icon(
              Icons.phone,
              size: 50,
            ),
            style: Theme.of(context).textTheme.headline6,
            onRightIconClicked: () => widget.store.onSetPhoneClicked(),
          )
        ],
      ),
    );
  }

  _onSaveDone() {
    Navigator.of(context).pop();
  }

  void _showDialog(BuildContext context,
      {required String title,
      required String description,
        String? initialValue,
      Widget? icon,
      required ValueChanged onSubmitted,
      TextInputType inputType = TextInputType.text}) async {
    showDialog(
        context: context,
        builder: (context) => CustomDialog(
              initialValue: initialValue,
              title: title,
              description: description,
              icon: icon,
              onSubmitted: onSubmitted,
              textInputType: inputType,
            ));
  }



  void _handleActionChange(CreateOrEditAction? action) {
    switch(action.runtimeType){
      case OnGateSaved: {
        print("GateSaved: ");
        _onSaveDone();
        break;
      }
      case ShowSetNameDialog: {
        print("ShowSetNameDialog: ${(action as ShowSetNameDialog).name}");
        _showDialog(context,
            initialValue: action.name,
            title: "Edit name",
            description: "Enter gate name",
            icon: Icon(
              Icons.description,
              size: 50,
            ), onSubmitted: (text) {
              print("onSubmitted: $text");
              widget.store.setName(text);
            });
        break;
      }
      case ShowEditPhoneDialog: {
        _showDialog(context,
            title: "Edit Phone",
            description: "Enter gate phone number",
            icon: Icon(
              Icons.phone,
              size: 50,
            ),
            onSubmitted: (text) => widget.store.setPhoneNumber(text),
            inputType: TextInputType.phone);
      }
    }
  }
}
