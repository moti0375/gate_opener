import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'package:gate_opener/channel/channel_adapter_impl.dart';
import 'package:gate_opener/data/datasources/gate_opener_platform_datasource.dart';
import 'package:gate_opener/data/repository/GateOpenerRepositoryImpl.dart';
import 'package:gate_opener/pages/home/home_page_bloc.dart';
import 'package:gate_opener/pages/home/home_page_event.dart';
import 'package:gate_opener/pages/home/home_page_state.dart';
import 'package:gate_opener/res/colors.dart';
import 'package:gate_opener/res/strings.dart';
import 'package:gate_opener/widgets/app_text_view.dart';
import 'package:gate_opener/widgets/gates_list_item.dart';

class HomePage extends StatefulWidget {
  const HomePage({Key? key}) : super(key: key);

  @override
  _HomePageState createState() => _HomePageState();

  static Widget create() {
    return BlocProvider(
      create: (_) => HomePageBloc(GateOpenerRepositoryImpl(
          GateOpenerPlatformDataSource(ChannelAdapterImpl()))),
      child: HomePage(),
    );
  }
}

class _HomePageState extends State<HomePage> {
  @override
  void initState() {
    super.initState();
    HomePageBloc bloc = context.read<HomePageBloc>();
    print("initState: $bloc");
    bloc.add(GetAllGates());
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      floatingActionButton: FloatingActionButton(onPressed: () => context.read<HomePageBloc>().add(AddGate()),child: Icon(Icons.add),),
      appBar: AppBar(
        elevation: 0,
        title: AppTextView(
          text: MY_GATES_TITLE,
        ),
      ),
      body: Center(
          child: BlocBuilder<HomePageBloc, HomePageState>(
        builder: (context, state) => _buildPageContent(state),
      )),
    );
  }

  Widget _buildPageContent(HomePageState state) {
    if (state is GatesLoaded) {
      return ListView.separated(
        itemBuilder: (context, index) => Dismissible(
            key: Key(state.gates[index].toString()),
            onDismissed: (direction) {
              context.read<HomePageBloc>().add(DeleteGate(state.gates[index].id));
            },
            direction: DismissDirection.horizontal,
            child: ListItem(
              null,
              itemViewModel: state.gates[index],
            )),
        separatorBuilder: (context, index) =>
            Divider(color: AppColors.GeneralDividerGray, height: 1),
        itemCount: state.gates.length,
      );
    } else {
      return Container();
    }
  }
}
