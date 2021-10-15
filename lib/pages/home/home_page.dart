import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:gate_opener/widgets/designed_button.dart';

class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  static const channel =
      MethodChannel("com.bartovapps.gate_opener.flutter.dev/channel");

  Future<void> _startService() async {
    try {
      print("_startService");
      await channel.invokeMethod("startService");
    } on MissingPluginException catch (e) {} on PlatformException catch (e) {}
  }

  Future<void> _stopService() async {
    try {
      print("_stopService");
      await channel.invokeMethod("stopService");
    } on MissingPluginException catch (e) {} on PlatformException catch (e) {}
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          mainAxisSize: MainAxisSize.min,
          children: [
            DesignedButton(
              text: "Start Service",
              onPressed: () => _startService(),
            ),
            SizedBox(
              height: 16,
            ),
            DesignedButton(
              text: "Stop Service",
              onPressed: () => _stopService(),
            )
          ],
        ),
      ),
    );
  }
}
