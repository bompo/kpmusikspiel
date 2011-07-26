package com.shader;

public class TVShader {

    public static final String mVertexShader =                    
    	  "attribute vec4 a_vertex;\n"
		+ "void main() {\n" 
		+ "  gl_Position = vec4(a_vertex);\n"
		+ "}\n";

    public static final String mFragmentShader =   
        "#ifdef GL_ES\n" +
        "precision mediump float;\n" +
        "#endif\n"
		+ "uniform float time;\n" 
		+ "uniform vec2 resolution;\n" 
		+ "uniform sampler2D sampler0;\n"
		+ "uniform float disort;\n"
		+ "void main(void) {\n"
		+ "vec2 position = gl_FragCoord.xy / resolution.xy;\n" 
		+ "position.y *=-1.0;\n" 
		+ "vec3 color;\n" 
		+ "//color separation\n"
		+ "	color.r = texture2D(sampler0,vec2(position.x+disort,-position.y)).x;\n" 
		+ "	color.g = texture2D(sampler0,vec2(position.x+0.000,-position.y)).y;\n"
		+ "	color.b = texture2D(sampler0,vec2(position.x-disort,-position.y)).z;\n"
		+ "//contrast\n" 
		+ "color = clamp(color*0.5+0.5*color*color*1.2,0.0,1.0);\n" 
		+ "//circular vignette fade\n"
		+ "color *= 0.5 + 0.5*16.0*position.x*position.y*(1.0-position.x)*(-1.0-position.y);\n" 
		+ "//color shift \n" 
		+ "color *= vec3(0.95,0.85,1.0); //blue\n" 
		+ "//tvlines effect\n"
		+ "color *= 0.9+0.1*sin(10.0*time+position.y*1000.0);\n" 
		+ "//tv flicker effect\n" 
		+ "color *= 0.97+0.03*sin(110.0*time);\n"
		+ "gl_FragColor = vec4(color,1.0);\n" 
		+ "}";

}