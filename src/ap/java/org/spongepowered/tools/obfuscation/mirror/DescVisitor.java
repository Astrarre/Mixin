package org.spongepowered.tools.obfuscation.mirror;

import java.util.function.Consumer;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.ErrorType;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.UnionType;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.AbstractTypeVisitor14;

public class DescVisitor extends AbstractTypeVisitor14<Void, Void> {
	StringBuilder desc = new StringBuilder();

	public static String getDesc(TypeMirror mirror) {
		DescVisitor visitor = new DescVisitor();
		mirror.accept(visitor, null);
		return visitor.desc.toString();
	}

	@Override
	public Void visitIntersection(IntersectionType t, Void unused) {
		return this.visit(t.getBounds().get(0));
	}

	@Override
	public Void visitPrimitive(PrimitiveType t, Void unused) {
		String value = getPrimitiveDesc(t.getKind());
		this.desc.append(value);
		return null;
	}

	public static String getPrimitiveDesc(TypeKind t) {
		switch(t) {
			case INT: return "I";
			case BYTE: return "B";
			case CHAR: return "C";
			case LONG: return "J";
			case VOID: return "V";
			case FLOAT: return "F";
			case SHORT: return "S";
			case DOUBLE: return "D";
			case BOOLEAN: return "Z";
			default: throw new UnsupportedOperationException("Unrecognized primitive type " + t);
		}
	}

	@Override
	public Void visitNull(NullType t, Void unused) {
		desc.append("Ljava/lang/Object;");
		return null;
	}

	@Override
	public Void visitArray(ArrayType t, Void unused) {
		this.desc.append('[');
		this.visit(t.getComponentType());
		return null;
	}

	@Override
	public Void visitDeclared(DeclaredType t, Void unused) {
		this.desc.append('L').append(TypeUtil.getInternalName(t.asElement())).append(';');
		return null;
	}

	@Override
	public Void visitError(ErrorType t, Void unused) {
		return null;
	}

	@Override
	public Void visitTypeVariable(TypeVariable t, Void unused) {
		this.visit(t.getUpperBound());
		return null;
	}

	@Override
	public Void visitWildcard(WildcardType t, Void unused) {
		var type = t.getExtendsBound();
		if(type != null) {
			this.visit(type);
		} else {
			this.desc.append("Ljava/lang/Object;");
		}
		return null;
	}

	@Override
	public Void visitExecutable(ExecutableType t, Void unused) {
		this.desc.append('(');
		for(TypeMirror type : t.getParameterTypes()) {
			this.visit(type);
		}
		this.desc.append(')');
		this.visit(t.getReturnType());
		return null;
	}

	@Override
	public Void visitNoType(NoType t, Void unused) {
		String desc;
		switch(t.getKind()) {
			case NONE: 
			case PACKAGE:
			case MODULE: 
				desc = "Ljava/lang/Object;";
				break;
			case VOID: 
				desc = "V"; 
				break;
			default:
				throw new UnsupportedOperationException("Unrecognized type " + t);
		};
		this.desc.append(desc);
		return null;
	}

	@Override
	public Void visitUnion(UnionType t, Void unused) {
		this.visit(t.getAlternatives().get(0));
		return null;
	}
}
