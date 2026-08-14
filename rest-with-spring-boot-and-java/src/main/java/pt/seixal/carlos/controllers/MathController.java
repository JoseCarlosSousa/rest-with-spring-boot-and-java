package pt.seixal.carlos.controllers;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.seixal.carlos.exceptions.UnsupportedMathOperationException;
import pt.seixal.carlos.math.SimpleMath;
import pt.seixal.carlos.request.converters.NumberConverter;

@RestController
@RequestMapping("/math")
public class MathController {

    private SimpleMath math = new SimpleMath();

    @RequestMapping("/sum/{numberOne}/{numberTwo}")
    public Double sum(@PathVariable("numberOne") String numberOne, @PathVariable("numberTwo")  String numberTwo) throws Exception {
        return math.sum(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
    }

    @RequestMapping("/sub/{numberOne}/{numberTwo}")
    public Double sub(@PathVariable("numberOne") String numberOne, @PathVariable("numberTwo")  String numberTwo) throws Exception {
        return math.subtraction(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
    }

    @RequestMapping("/multi/{numberOne}/{numberTwo}")
    public Double multi(@PathVariable("numberOne") String numberOne, @PathVariable("numberTwo")  String numberTwo) throws Exception {
        return math.multiplication(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
    }

    @RequestMapping("/div/{numberOne}/{numberTwo}")
    public Double div(@PathVariable("numberOne") String numberOne, @PathVariable("numberTwo")  String numberTwo) throws Exception {
        return math.division(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
    }

    @RequestMapping("/mean/{numberOne}/{numberTwo}")
    public Double mean(@PathVariable("numberOne") String numberOne, @PathVariable("numberTwo")  String numberTwo) throws Exception {
        return math.mean(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
    }

    @RequestMapping("/squareRoot/{numberOne}")
    public Double squareRoot(@PathVariable("numberOne") String numberOne) throws Exception {
        return math.squareRoot(NumberConverter.convertToDouble(numberOne));
    }


}
