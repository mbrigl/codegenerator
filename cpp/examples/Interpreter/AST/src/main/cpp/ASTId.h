/* Generated By:JJTree: Do not edit this line. ASTId.h Version 8.0 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=true,NODE_PREFIX=AST,NODE_EXTENDS=MyNode,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
#pragma once

#include "Node.h"


class ASTId : public Node {
public: 
  ASTId(int id);
  virtual ~ASTId();
  
  virtual void interpret();
  friend class SPLParser;
  friend class ASTAssignment;

private:
	string name;
};

/* JavaCC - OriginalChecksum=84b3499f66fd2794baef80957d1c4ce9 (do not edit this line) */
