package me.andimeo;

class FilterConditionException extends Exception {
	private static final long serialVersionUID = 335961760317711784L;
}

class TimeUnitException extends FilterConditionException {
	private static final long serialVersionUID = -5064557213611871204L;
}

class DateException extends FilterConditionException {
	private static final long serialVersionUID = -3026880555915279806L;
}
