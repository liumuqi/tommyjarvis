package fsmengine

import (
	//. "./fsmengine"
	"encoding/json"
	"fmt"
	"reflect"
)

var StartEvent = "start"
var ContinueEvent = "continue"
var PauseEvent = "pause"
var RollbackEvent = "rollback"
var AutoSuccessEvent = "autoSuccess"
var AutoFailEvent = "autoFail"
var NoneEvent = "none"

type releaseEngineServiceImpl struct {
	fsm *StateMachine
}

func (s *releaseEngineServiceImpl) Test(t int32, batchNum int32, aa int32, bb int32, s1 int32, s2 int32) (bool, error) {

	//s.fsm.NextTrigger(3, StartEvent, int32(111111), int32(2222222), int32(3333333), int32(4444444))
	fmt.Printf("%v,%v,%v,%v,%v,%v", t, batchNum, aa, bb, s1, s2)
	return true, nil
}

func (s *releaseEngineServiceImpl) BBB(t int32, batchNum int32, aa int32, bb int32, s1 int32, s2 int32) (bool, error) {
	fmt.Printf("%v,%v,%v,%v,%v,%v", t, batchNum, aa, bb, s1, s2)
	return true, nil
}

func main() {
	value := "cat;dog"
	// Take substring from index 4 to length of string.
	substring := value[4 : len(value)-1]

	pairs := make([][]int32, 1)
	pairs[0] = make([]int32, 2)
	pairs[0][0] = int32(-1)
	pairs[0][1] = int32(-1)



	//(1,2),(3,4)

	p := ""
	for _, pair := range pairs {
		p = p + "," + fmt.Sprintf("(%v,%v)", pair[0], pair[1])
	}


	data:="{\"1\":{\"2\":{\"status\":1,\"type\":2,\"preversion\":\"回滚/发布\"}},\"2\":{\"2\":{\"status\":1,\"type\":2,\"preversion\":\"之前版本\"}}}"
	var result map[string]interface{}
	json.Unmarshal([]byte(data), &result)
	fmt.Println(result)
	m := result["1"].(map[string]interface{})["2"].(map[string]interface{})["preversion"]
	fmt.Println(m)

	fmt.Println(substring)
	tt := make([]string, 12)
	fmt.Println(len(tt))

	impl := releaseEngineServiceImpl{}
	fsm := initFSM(&impl)
	impl.fsm = fsm
	fsm.Trigger(int32(1), AutoSuccessEvent, int32(3), int32(4), int32(5), int32(6))
}

//执行顺序 (离开当前状态动作ExitAction)->进入当前状态动作Action->上步失败处理OnActionFailure->进入目标状态动作EnterAction
func initFSM(processor *releaseEngineServiceImpl) *StateMachine {
	delegate := &DefaultDelegate{P: processor}
	transitions := []Transition{
		Transition{From: 1, Event: AutoSuccessEvent, To: 2, Action: "Test", ExitAction: "", EnterAction: ""},
		Transition{From: 3, Event: StartEvent, To: 4, Action: "BBB", ExitAction: "", EnterAction: ""},
	}
	return NewStateMachine(delegate, transitions...)
}

func (s *releaseEngineServiceImpl) Action(action string, fromState int32, toState int32, args []interface{}) (bool, error) {
	canPass := true
	var err error
	if len(action) > 0 {
		args = append(args, fromState)
		args = append(args, toState)
		canPass, err = InvokeObjectMethod(s, action, args...)
	}
	return canPass, err
}

func (s *releaseEngineServiceImpl) OnActionFailure(action string, fromState int32, toState int32, args []interface{}, err error) (bool, error) {
	canPass := true
	if len(action) > 0 {
		args = append(args, fromState)
		args = append(args, toState)
		canPass, err = InvokeObjectMethod(s, action, args...)
	}
	return canPass, err
}

func (s *releaseEngineServiceImpl) OnExit(leaveAction string, fromState int32, toState int32, args []interface{}) (bool, error) {
	canPass := true
	var err error
	if len(leaveAction) > 0 {
		args = append(args, fromState)
		args = append(args, toState)
		canPass, err = InvokeObjectMethod(s, leaveAction, args...)
	}
	return canPass, err
}

func (s *releaseEngineServiceImpl) OnEnter(enterAction string, fromState int32, toState int32, args []interface{}) (bool, error) {
	canPass := true
	var err error
	if len(enterAction) > 0 {
		args = append(args, fromState)
		args = append(args, toState)
		canPass, err = InvokeObjectMethod(s, enterAction, args...)
	}
	return canPass, err
}

//InvokeObjectMethod 调用的方法
func InvokeObjectMethod(object interface{}, methodName string, args ...interface{}) (bool, error) {
	inputs := make([]reflect.Value, len(args))
	for i, _ := range args {
		inputs[i] = reflect.ValueOf(args[i])
	}
	call := reflect.ValueOf(object).MethodByName(methodName).Call(inputs)
	var err error
	if v, ok := call[1].Interface().(error); ok {
		err = v
	}
	return call[0].Bool(), err
}
